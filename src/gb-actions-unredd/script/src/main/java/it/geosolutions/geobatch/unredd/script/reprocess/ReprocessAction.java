/*
 *  GeoBatch - Open Source geospatial batch processing system
 *  https://github.com/nfms4redd/nfms-geobatch
 *  Copyright (C) 2007-2012 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 *  GPLv3 + Classpath exception
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.geosolutions.geobatch.unredd.script.reprocess;

import it.geosolutions.filesystemmonitor.monitor.FileSystemEvent;
import it.geosolutions.filesystemmonitor.monitor.FileSystemEventType;
import it.geosolutions.geobatch.flow.event.action.ActionException;
import it.geosolutions.geobatch.flow.event.action.BaseAction;
import it.geosolutions.geobatch.geotiff.overview.GeotiffOverviewsEmbedderConfiguration;
import it.geosolutions.geobatch.unredd.script.exception.FlowException;
import it.geosolutions.geobatch.unredd.script.exception.GeoStoreException;
import it.geosolutions.geobatch.unredd.script.model.RasterizeConfig;
import it.geosolutions.geobatch.unredd.script.reprocess.model.ReprocessChartRequest;
import it.geosolutions.geobatch.unredd.script.reprocess.model.ReprocessLayerRequest;
import it.geosolutions.geobatch.unredd.script.reprocess.model.ReprocessRequest;
import it.geosolutions.geobatch.unredd.script.reprocess.model.ReprocessStatsRequest;
import it.geosolutions.geobatch.unredd.script.reprocess.model.RequestReader;
import it.geosolutions.geobatch.unredd.script.util.FlowUtil;
import it.geosolutions.geobatch.unredd.script.util.GeoStoreUtil;
import it.geosolutions.geobatch.unredd.script.util.GeoTiff;
import it.geosolutions.geobatch.unredd.script.util.Statistics.Tokens;
import it.geosolutions.geobatch.unredd.script.util.rasterize.GDALRasterize;
import it.geosolutions.geostore.core.model.Resource;
import it.geosolutions.unredd.geostore.model.UNREDDFormat;
import it.geosolutions.unredd.geostore.model.UNREDDLayer;
import it.geosolutions.unredd.geostore.model.UNREDDLayer.Attributes;
import it.geosolutions.unredd.geostore.model.UNREDDLayerUpdate;
import it.geosolutions.unredd.geostore.model.UNREDDStatsDef;
import it.geosolutions.unredd.geostore.utils.NameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ETj ( etj@geo-solutions.it )
 * @author Luca Paolino - luca.paolino@geo-solutions.it
 *
 */
public class ReprocessAction extends BaseAction<FileSystemEvent> {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReprocessAction.class);

    /**
     * configuration
     */
    private final ReprocessConfiguration conf;

    private GeoStoreUtil geoStoreUtil = null;


    public ReprocessAction(ReprocessConfiguration configuration) {
        super(configuration);
        conf = configuration;
    }

    /**
     * Main loop on input files. Single file processing is called on execute(File xmlFile)
     */
    public Queue<FileSystemEvent> execute(Queue<FileSystemEvent> events)
            throws ActionException {


        if(getTempDir() == null) {
            throw new IllegalStateException("temp dir has not been initialized");
        }
        if(! getTempDir().exists()) {
            throw new IllegalStateException("temp dir does not exist");
        }

        geoStoreUtil = new GeoStoreUtil(conf.getGeoStoreConfig(), getTempDir());


//        initComponents(properties);

        final Queue<FileSystemEvent> ret = new LinkedList<FileSystemEvent>();

        while (!events.isEmpty()) {
            final FileSystemEvent ev = events.remove();

            try {
                if (ev != null) {
                    if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace("Processing incoming event: " + ev.getSource());
                    }
                    File xmlFile = ev.getSource(); // this is the input xml file

                    /**
                     * *************************
                     * The reprocessing flow will recompute statistics and charts. it is needed when data in the staging area are
                     * changed; i.e.: - vector data are edited; - chart scripts are modified or inserted; - new statistics are
                     * added. Each doXXX methos manages one of this case
                     */
                    ReprocessRequest request = RequestReader.load(xmlFile);
                    if (request == null) {
                        throw new ActionException(this, "Could not parse input file:" + xmlFile.getName());
                    }

                    if (request instanceof ReprocessLayerRequest) {
                        reprocessLayer((ReprocessLayerRequest) request);

                    } else if (request instanceof ReprocessChartRequest) {
                        reprocessChart((ReprocessChartRequest) request);

                    } else if (request instanceof ReprocessStatsRequest) {
                        reprocessStats((ReprocessStatsRequest) request);

                    }

                    ret.add(new FileSystemEvent(xmlFile, FileSystemEventType.FILE_ADDED));

                } else {
                    LOGGER.error("Encountered a null event: skipping event");
                    continue;
                }

            } catch (ActionException ex) {
                LOGGER.error(ex.getMessage());
                listenerForwarder.failed(ex);
                throw ex;

            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
                listenerForwarder.failed(ex);
                throw new ActionException(this, ex.getMessage(), ex);
            }
        }

        return ret;
    }

    /**
     * ******************
     * manage the reprocess execution in the case of a StatsDef updates. For each statsdef searches all the layer associated, for
     * each layer looks for its layer updates for each layer update run the stats update chartscript
     *
     * @param request, this contains the list of the StatsDef to manage
     * @throws ActionException
     */
    private void reprocessStats(ReprocessStatsRequest request) throws Exception {

        List<String> statsDefNames = request.getStatsNames();
        if (statsDefNames == null || statsDefNames.isEmpty()) {
            LOGGER.info("No StatsDef to reprocess");
        }

        Set<Resource> chartScripts = new HashSet<Resource>();

        // Loop on all requested stats
        int cnt = 0;
        for (String statsDefName : statsDefNames) {
            float min = rescale(0, 50, cnt++/statsDefNames.size());
            float max = rescale(0, 50, cnt/statsDefNames.size());
            Resource statsDefRes = geoStoreUtil.searchStatsDefByName(statsDefName);
            if (statsDefRes == null) {
                LOGGER.warn("StatsDef not found: " + statsDefName);
                continue;
            }
            reprocessStat(statsDefRes, min, max);

            List<Resource> localChartScript = geoStoreUtil.searchChartScriptByStatsDef(statsDefName);
            chartScripts.addAll(localChartScript);
        }

        LOGGER.info("Starting reprocessing ChartScripts");
        this.listenerForwarder.progressing(50f, "Starting reprocessing ChartScripts");

        final FlowUtil flowUtil = new FlowUtil(getTempDir(), getConfigDir());
        flowUtil.runScripts(geoStoreUtil, chartScripts);

        LOGGER.info("Reprocessing ChartScripts completed");
        this.listenerForwarder.setTask("Reprocessing ChartScripts completed");
        this.listenerForwarder.completed();
    }

    private void reprocessStat(Resource statsDefRes, float min, float max) throws GeoStoreException, FlowException {
        String statsDefName = statsDefRes.getName();
        LOGGER.info("Starting reprocessing StatsDef '" + statsDefName+ "'");
        this.listenerForwarder.progressing(rescale(min, max, 0), "Starting reprocessing StatsDef '" + statsDefName+ "'");

        UNREDDStatsDef statsDef = new UNREDDStatsDef(statsDefRes);
        List<String> layerNames = statsDef.getReverseAttributesInternal(UNREDDStatsDef.ReverseAttributes.LAYER);

        if( layerNames.isEmpty()) {
            LOGGER.warn("No layers defined for StatsDef '" + statsDefName+ "'");
        }

        final FlowUtil flowUtil = new FlowUtil(getTempDir(), getConfigDir());

        // Loop on all layers on which this stat depends on
        int layers = layerNames.size();
        int layerCnt = 0;

        for (String layerName : layerNames) {
            float majorStep = layerCnt++ / layers;
            LOGGER.info("Starting reprocessing StatsDef:" + statsDefName + " layer:" + layerName);
            this.listenerForwarder.progressing(rescale(min, max, majorStep), "Reprocessing stats on layer " + layerName);

            Resource layerRes = geoStoreUtil.searchLayer(layerName);
            if(layerRes == null) {
                LOGGER.warn("Could not find Layer '"+layerName+"' for StatsDef '"+statsDefName+"'");
                continue;
            }
            UNREDDLayer layer = new UNREDDLayer(layerRes);

            List<Resource> layerUpdates = geoStoreUtil.searchLayerUpdateByLayer(layerName);
            LOGGER.info("Found " + layerUpdates.size() + " LayerUpdates for Layer " + layerName);

            // Compute stats for every time coordinate this layer has
            int layerUps = layerUpdates.size();
            int layerUpsCnt = 0;
            for (Resource layerUpdateRes : layerUpdates) {
                float minorStep = layerUpsCnt++ / layerUps;
                String msg = "Starting reprocessing StatsDef:" + statsDefName + " Layer:" + layerName + " LayerUpdate:" + layerUpdateRes.getName();
                LOGGER.info(msg);
                this.listenerForwarder.progressing(rescale(min, max, majorStep*(1+minorStep)), msg);

                UNREDDLayerUpdate layerUpdate = new UNREDDLayerUpdate(layerUpdateRes);
                String year = layerUpdate.getAttribute(UNREDDLayerUpdate.Attributes.YEAR);
                String month = layerUpdate.getAttribute(UNREDDLayerUpdate.Attributes.MONTH);
                String day = layerUpdate.getAttribute(UNREDDLayerUpdate.Attributes.DAY);

                String rasterPath = layer.getAttribute(Attributes.MOSAICPATH);
                String rasterFile = NameUtils.buildTifFileName(layerName, year, month, day);
                String rasterFullPath = new File(rasterPath, rasterFile).getAbsolutePath();

                Map<Tokens, String> tokens = FlowUtil.fillTokens(rasterFullPath, layerName, year, month, null);
                flowUtil.processStatistics(geoStoreUtil, statsDefRes, year, month, day, tokens);
            }
        }

        LOGGER.info("Reprocessing completed on StatsDef '" + statsDefName+ "'");
        this.listenerForwarder.progressing(max, "Reprocessing completed on StatsDef '" + statsDefName+ "'");
    }

    protected static float rescale(float min, float max, float current) {
        return min + (max-min)*current;
    }

    /**
     * **********
     * this method manages the reprocess in case of script changes.
     *
     * @param request
     * @throws ActionException
     */
    public void reprocessChart(ReprocessChartRequest request) throws Exception {

        this.listenerForwarder.setTask("Starting reprocessChart");
        List<Resource> chartScriptList = new ArrayList(request.getChartNames().size());

        for (String chartScriptName : request.getChartNames()) {
            listenerForwarder.setTask("Processing requested chart " + chartScriptName);
            Resource chartScriptResource = geoStoreUtil.searchChartScript(chartScriptName);
            if(chartScriptResource == null) {
                LOGGER.warn("Could not find chart '"+chartScriptName+"'. Will be skipped.");
                listenerForwarder.setTask("Skipping unknown chart " + chartScriptName);
                listenerForwarder.progressing();
                continue;
            }

            if(LOGGER.isDebugEnabled())
                LOGGER.debug(" got info for chartScript to reprocess -->" + chartScriptName);
            chartScriptList.add(chartScriptResource);
        }

        FlowUtil flowUtil = new FlowUtil(getTempDir(), getConfigDir());
        flowUtil.runScripts(geoStoreUtil, chartScriptList);
    }

    /******************
     * Reprocess layer.
     * Procedure steps: <ul>
     * <li> Check Layer </li>
     * <li> Creates LayerUpdates if needed </li>
     * <li> If Vector  => Rasterize, embed overviews, move into mosaic dir </li>
     * <li> <B>TODO</B>: check if mosaic tile properly configured in geoserver </li>
     * <li> Start reprocess stats </li>
     * </ul>
     *
     */
    private void reprocessLayer(ReprocessLayerRequest request) throws ActionException {

        LOGGER.info("Started layer reprocessing");

        String layerName = request.getLayerName();
        String year = request.getYear();
        String month = request.getMonth();
        String day = request.getDay();

        LOGGER.info("Reprocessing layer:" + layerName + " year:" + year + " month:" + month);

        // ========================================
        // Load layer info

        LOGGER.info("Searching " + layerName + " in Geostore");
        Resource layerRes = null;
        try {
            layerRes = geoStoreUtil.searchLayer(layerName);
        } catch (GeoStoreException e) {
            throw new ActionException(this, "Error while searching layer: " + layerName, e);
        }

        if (layerRes == null) {
            throw new ActionException(this, "Layer not found: " + layerName);
        }

        UNREDDLayer layer = new UNREDDLayer(layerRes);
        LOGGER.info(layerName + " found in the Staging Area Geostore");


        // ========================================
        // Load layerUpdate

        LOGGER.info("Searching layer update [" + layerName + ", " + year + "," + month + "]");

        Resource layerUpdatesRes = null;
        try {
            layerUpdatesRes = geoStoreUtil.searchLayerUpdate(layerName, year, month, day);
        } catch (GeoStoreException e) {
            throw new ActionException(this, "Error while searching LayerUpdate: " + layerName, e);
        }

        if (layerUpdatesRes == null) {
            LOGGER.warn("Missing Layer update [" + layerName + ", " + year + "," + month + "]");

            // create the missing LayerUpdate entry
            try {
                geoStoreUtil.insertLayerUpdate(layerName, year, month, day);
            } catch (GeoStoreException e) {
                throw new ActionException(this, "Error while inserting a LayerUpdate", e);
            }
        }

        try {
            layerUpdatesRes = geoStoreUtil.searchLayerUpdate(layerName, year, month, day);
        } catch (GeoStoreException e) {
            throw new ActionException(this, "LayerUpdate not createds: " + layerName, e);
        }

        UNREDDLayerUpdate layerUpdate = new UNREDDLayerUpdate(layerUpdatesRes);

        // ========================================
        // In case of update we should regenerate the tiff.
        // We have also retile, overview it and move it to the mosaic directory

        File rasterfile = null;
        String layerFormat = layer.getAttribute(Attributes.LAYERTYPE);

        if (UNREDDFormat.VECTOR.name().equals(layerFormat)) {

            rasterfile = reprocessVector(layer, layerUpdate);

        } else if (UNREDDFormat.RASTER.name().equals(layerFormat)) {

            File dir = new File(layer.getAttribute(UNREDDLayer.Attributes.MOSAICPATH));
            String filename = NameUtils.buildTifFileName(layerName, year, month, day);
            rasterfile = new File(dir, filename);

        } else {
            throw new ActionException(this, "Unrecognized layer format '"+layerFormat+"'");
        }

        // ========================================
        // Compute all related statsData and chartData

        try {
            FlowUtil flowUtil = new FlowUtil(getTempDir(), getConfigDir());
            flowUtil.runStatsAndScripts(layerName, year, month, day, rasterfile, geoStoreUtil);
        } catch (FlowException e) {
            throw new ActionException(this, e.getMessage(), e);
        }

    }

    /**
     * Regenerate the raster file by reading the features from a PostGISUtils datastore.
     *
     */
    public File reprocessVector(UNREDDLayer layer, UNREDDLayerUpdate layerUpdate) throws ActionException {

        String layername = layerUpdate.getAttribute(UNREDDLayerUpdate.Attributes.LAYER);
        String year      = layerUpdate.getAttribute(UNREDDLayerUpdate.Attributes.YEAR);
        String month     = layerUpdate.getAttribute(UNREDDLayerUpdate.Attributes.MONTH);
        String day     = layerUpdate.getAttribute(UNREDDLayerUpdate.Attributes.DAY);

        // ========================================
        // Rasterize
        // ========================================
        LOGGER.info("Regenerating raster for " + NameUtils.buildLayerUpdateName(layername, year, month, day));

        File rasterFile = null;
        try {
            RasterizeConfig rasterizeConfig = conf.getRasterizeConfig();
            GDALRasterize rasterize = new GDALRasterize(rasterizeConfig, conf.getConfigDir(), this.getTempDir());
            rasterFile = rasterize.run(layer, layerUpdate, conf.getPostGisConfig());

        } catch(Exception e) {
            throw new ActionException(this, "Exception while rasterizing: " + e.getMessage(), e);
        }

        // ========================================
        // Embed overviews
        // ========================================
        LOGGER.info("Embedding overviews for " + NameUtils.buildLayerUpdateName(layername, year, month, day) + " in " + rasterFile);

        GeotiffOverviewsEmbedderConfiguration ovCfg = conf.getOverviewsEmbedderConfiguration();

        try {
            GeoTiff.embedOverviews(ovCfg, rasterFile, getTempDir());
        } catch (Exception e) {
            throw new ActionException(this, "Exception while embedding overviews in " + rasterFile.getName(), e);
        }

        // ========================================
        // Move to mosaic dir
        // ========================================

        String mosaicPath = layer.getAttribute(Attributes.MOSAICPATH);

        String finalName = NameUtils.buildTifFileName(layername, year, month, day);
        File finalPath = new File(mosaicPath, finalName);
        if(finalPath.exists())
            LOGGER.info("Overwriting old raster:" + finalPath);
        else
            LOGGER.info("Old raster does not exist:" + finalPath);

        try {
            FileUtils.copyFile(rasterFile, finalPath);
        } catch(IOException e) {
            throw new ActionException(this, "Exception while moving raster (src: " + rasterFile + " dst:"+finalPath+')', e);
        }

        return finalPath;
    }

    @Override
    public boolean checkConfiguration() {
        return true;
    }

}
