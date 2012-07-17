/*
 *  GeoBatch - Open Source geospatial batch processing system
 *  https://github.com/nfms4redd/nfms-geobatch
 *  Copyright (C) 2007-2008-2009 GeoSolutions S.A.S.
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

package it.geosolutions.geobatch.unredd.script.publish;

import it.geosolutions.filesystemmonitor.monitor.FileSystemEvent;
import it.geosolutions.filesystemmonitor.monitor.FileSystemEventType;
import it.geosolutions.geobatch.flow.event.action.ActionException;
import it.geosolutions.geobatch.flow.event.action.BaseAction;
import it.geosolutions.geobatch.unredd.script.exception.GeoStoreException;
import it.geosolutions.geobatch.unredd.script.exception.PostGisException;
import it.geosolutions.geobatch.unredd.script.model.PostGisConfig;
import it.geosolutions.geobatch.unredd.script.model.Request;
import it.geosolutions.geobatch.unredd.script.util.FlowUtil;
import it.geosolutions.geobatch.unredd.script.util.GeoStoreUtil;
import it.geosolutions.geobatch.unredd.script.util.PostGISUtils;
import it.geosolutions.geobatch.unredd.script.util.RequestJDOMReader;
import it.geosolutions.unredd.geostore.model.UNREDDFormat;
import it.geosolutions.unredd.geostore.model.UNREDDLayer;

import it.geosolutions.geostore.core.model.Resource;
import it.geosolutions.geostore.services.dto.ShortResource;
import it.geosolutions.geostore.services.rest.model.RESTResource;
import it.geosolutions.unredd.geostore.utils.NameUtils;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.xml.bind.JAXBException;
import org.apache.commons.io.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * performs the publishing of a layer. It takes an xml file about the layer to publish and copy all the resources from the staging area to the dissemination area
 *
 * @author Luca Paolino - luca.paolino@geo-solutions.it
 *
 */
public class PublishingAction extends BaseAction<FileSystemEvent> {

    private final static Logger LOGGER = LoggerFactory.getLogger(PublishingAction.class);

//    private String srcRepositoryPath = null;
//    private String srcMosaicPath = null;
//
//    private PostGISUtils postGISTo = null;
//    private PostGISUtils postGISMosaicTo = null;
//    private String dissRepositoryPath = null;
//    private String dissMosaicPath = null;
//
    private GeoStoreUtil srcGeostore = null;
    private GeoStoreUtil dstGeostore = null;
    /**
     * configuration
     */
    private final PublishingConfiguration conf;


    public PublishingAction(PublishingConfiguration configuration)
            throws JAXBException, IOException, ActionException {
        super(configuration);
        conf = configuration;

    }

    public void initialize() {
        srcGeostore = new GeoStoreUtil(conf.getSrcGeoStoreConfig(), getTempDir());
        dstGeostore = new GeoStoreUtil(conf.getDstGeoStoreConfig(), getTempDir());
    }

    /**
     * Main loop on input files. Single file processing is called on
     * execute(File xmlFile)
     */
    public Queue<FileSystemEvent> execute(Queue<FileSystemEvent> events)
            throws ActionException {

        // ****************************************
        // initialize PostGISUtils, Geostore and paths
        //
        // ****************************************

        try {
            initialize();
        } catch (Exception e) {
            LOGGER.error("Exception during component initialization", e);
            throw new ActionException(this, "Exception during initialization");
        }



        final Queue<FileSystemEvent> ret = new LinkedList<FileSystemEvent>();

        while (!events.isEmpty()) {
            final FileSystemEvent ev = events.remove();

            try {
                if (ev != null) {
                    if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace("PublishingAction.execute(): working on incoming event: " + ev.getSource());
                    }

                    File xmlFile = ev.getSource(); // this is the input xml file
                    execute(xmlFile);
                    ret.add(new FileSystemEvent(xmlFile, FileSystemEventType.FILE_ADDED));

                } else {
                    LOGGER.error("PublishingAction.execute(): Encountered a NULL event: SKIPPING...");
                    continue;
                }

            } catch (ActionException ex) { // ActionEx have already been processed
                LOGGER.error(ex.getMessage(), ex);
                throw ex;

            } catch (Exception ex) {
                final String message = "PublishingAction.execute(): Unable to produce the output: "
                        + ex.getLocalizedMessage();
                LOGGER.error(message, ex);
                throw new ActionException(this, message);
            }
        }

        return ret;
    }

    protected void execute(File xmlFile) throws ActionException, GeoStoreException {

        //=== Parse input file

        Request request = null;
        try {
            request = RequestJDOMReader.parseFile(xmlFile);
        } catch (Exception e) {
            throw new ActionException(this, "Exception parsing input file " + xmlFile.getName());
        }

        String layerName = request.getLayername();
        String year = request.getYear();
        String month = request.getMonth();

        LOGGER.info("Input parameters : [layer=" + layerName + ", year=" + year + ", month=" + month + "]");


        // ****************************************
        // Load source Layer info
        //
        // ****************************************

        LOGGER.info("Searching source Layer " + layerName);

        Resource srcLayer = srcGeostore.searchLayer(layerName);
        if(srcLayer == null) {
            throw new ActionException(this, "Source Layer not found [" + layerName+ "]");
        }

        UNREDDLayer layerResource = new UNREDDLayer(srcLayer);

        LOGGER.info(layerName + " found in the Staging Area Geostore");

        // TODO ***** add layer in destination if it does not exist
        LOGGER.error("TODO: add layer in destination if it does not exist");

        // ****************************************
        // check source layer update
        //
        // ****************************************

        LOGGER.info("Searching source LayerUpdate [" + layerName + ", " + year + "," + month + "]");

        Resource srcLayerUpdatesSA = srcGeostore.searchLayerUpdate(layerName, year, month);
        if (srcLayerUpdatesSA == null) {
            throw new ActionException(this, "Source LayerUpdate not found [" + layerName + ", " + year + "," + month + "]");
        }

            LOGGER.info("Source LayerUpdate found [" + layerName + ", " + year + "," + month + "]");

        // ****************************************
        // Copy the features
        // ----------
        // Copy feats identified by layer, year and month
        // from the staging area postgis db to the dissemination db
        // in case update is set on true we should
        // firstly remove the old features from the dissemination db.
        // This controls will be made directly in the updatePostGIS method
        // ****************************************
        boolean isVector = request.getFormat().equals(UNREDDFormat.VECTOR);

        if (isVector) {
            LOGGER.info("The request is VECTOR format based. Updating PostGIS");
            updatePostGIS(conf.getSrcPostGisConfig(), conf.getDstPostGisConfig(), layerName, year, month);
        } else {
            LOGGER.info("The request is RASTER format based.");
        }

        // ****************************************
        // Copy the raster
        // ----------
        // copies the raster located into the mosaic
        // staging area directory to the dissemination mosaic.
        // This  operation is performed both in case of a first publishing
        // and successive ones
        //
        // ****************************************
        try {
            // copy the raster file from the mosaic staging area directory to the dissemination mosaic directory
            String srcPath = layerResource.getAttribute(UNREDDLayer.Attributes.MOSAICPATH);
            String dstPath = layerResource.getAttribute(UNREDDLayer.Attributes.DISSMOSAICPATH);

            LOGGER.info("Copying mosaic raster from " + srcPath + " to " + dstPath);
            
            this.copyRaster(srcPath, dstPath, layerName, year, month);
            LOGGER.debug("Mosaic raster upload successfully completed");
//            if (!doUpdate) {
                LOGGER.warn("TODO: remove previous entries if needed");

                LOGGER.info("Copying Mosaic snapshot");
                // in case of publishing of a new snapshot in the mosaic dissemination, insert the db entry in the mosaic postgis dissemination db as well
//                this.copyMosaicDBEntry(layerName, year, month);
                PostGISUtils.copyFeatures(conf.getSrcPostGisConfig(), conf.getDstPostGisConfig(), layerName, year, month, true);

                LOGGER.debug("Mosaic snapahost copy successfully completed");
//            }
        } catch (PostGisException e) {
            LOGGER.debug("Property settings : [Layer = " + layerName + ", year = " + year + ", month=" + month + "]");
            throw new ActionException(this, "Error while copying features", e );
        } catch(IOException e) {
            throw new ActionException(this, "Error while copying raster", e );
        }

        // ****************************************
        // Copy GeoStoreUtil data
        // ----------
        // copy the resources related to the layer update
        // resource identified by layer, year and month to the
        // dissemination geostore
        // ****************************************

        // TODO: to complete using the setData

        try {
            LOGGER.info("Copying Geostore content");
            this.copyGeostoreData(srcGeostore, dstGeostore, layerName, year, month);
            LOGGER.debug("Geostore copy successfully completed");
        } catch (Exception e) {
            throw new ActionException(this, "Error while copying GeoStore content",e );
        }


        // ****************************************
        // Copy original data
        // ----------
        // copies the content of the staging area repository directory
        // to the dissemination repository directory.
        // This part is executed only in case of first
        // publishing because additional information cannot be
        // modified after ingestion
        // ****************************************

        LOGGER.error("TODO: copy original data");
//        try {
////            if (!doUpdate) {
//            LOGGER.warn("TODO: check destination does not exist");
//                this.copyOriginalData(srcRepositoryPath, dissRepositoryPath, layerResource.getAttribute(UNREDDLayer.Attributes.MOSAICPATH), layerName, year, month);
////            }
//        } catch (Exception e) {
//            LOGGER.debug("Property settings : [Source repository = " + UNREDDProps.REPOSITORYPATHS.propName() + ", Target repository = " + UNREDDPropsPublish.REPOSITORYPATHD.propName() + "]");
//            LOGGER.debug("Property settings : [Layer = " + layerName + ", year = " + year + ", month=" + month + "]");
//            throw new ActionException(this, "Error while copying original data",e );
//        }
    }


    public void disposeDataStore() {
//        if (postGISTo != null) {
//            postGISTo.getPgDatastore().dispose();
//        }
////        if (postGISMosaicTo != null) {
////            postGISMosaicTo.getPgDatastore().dispose();
////        }
//        if (postGISFrom != null) {
//            postGISFrom.getPgDatastore().dispose();
//        }
////        if (postGISMosaicFrom != null) {
////            postGISMosaicFrom.getPgDatastore().dispose();
////        }
    }


    /**
     * Copy some data from the staging GeoStoreUtil into the dissemination GeoStoreUtil: <UL>
     * <LI> Copy    1 Layer       if needed </LI>
     * <LI> Copy    1 LayerUpdate if needed </LI>
     * <LI> Replace N StatsDef    every time, layer deps may have changed</LI>
     * <LI> Replave N StatsData   every time</LI>
     * <LI> Replace M ChartScript every time, stats deps may have changed </LI>
     * <LI> Replave P ChartData   every time</LI>
     * </UL>
     *
     * @param srcGeostore
     * @param dstGeostore
     * @param layerName
     * @param year
     * @param month
     * 
     * @throws IOException
     * @throws ActionException
     * @throws JAXBException
     * @throws GeoStoreException
     */
    public void copyGeostoreData(GeoStoreUtil srcGeostore, GeoStoreUtil dstGeostore,
            String layerName, String year, String month)
                throws ActionException, GeoStoreException {

        //==================================================
        //=== LAYER
        //===
        //=== Insert Layer if it does not exist

        Resource srcLayer = srcGeostore.searchLayer(layerName);
        if (srcLayer == null) {
            throw new ActionException(this, "Source Layer not found [layer:" + layerName + "]");
        }

        //=== COPY LAYER

        Resource dstLayer = dstGeostore.searchLayer(layerName);
        if( dstLayer == null) {
            LOGGER.info("Copying Layer into destination: " + srcLayer);
            RESTResource layer = FlowUtil.copyResource(srcLayer);
            dstGeostore.insert(layer);
        }

        //==================================================
        //=== LAYERUPDATE
        //===
        //=== Insert LayerUpdate if it does not exist

        Resource srcLayerUpdate = srcGeostore.searchLayerUpdate(layerName, year, month);
        if (srcLayerUpdate == null) {
            throw new ActionException(this, "Source LayerUpdate not found [layer:" + layerName + " year:" + year + " month:" + month + "]");
        }

        //=== COPY LAYERUPDATE

        Resource dstLayerUpdate = dstGeostore.searchLayerUpdate(layerName, year, month);
        if ( dstLayerUpdate == null ) {
            LOGGER.info("Copying LayerUpdate " + layerName+":"+year+":"+month);

            RESTResource layerUpdate = FlowUtil.copyResource(srcLayerUpdate);
            dstGeostore.insert(layerUpdate);
        }

        //==================================================
        //=== STATS
        //===
        //=== loop on all the statistics associated with the layer

        Map<Long, Resource> srcChartScripts = new HashMap<Long, Resource>();

        List<ShortResource> statsDefList = srcGeostore.searchStatsDefByLayer(layerName, true); // may be empty, is always not null

        for (ShortResource statDef : statsDefList) {
            String statsDefName = statDef.getName();
            LOGGER.info("Found stats def :" + statsDefName);

            //=== OVERWRITE STATSDEF

            Resource dstStatsDef = dstGeostore.searchStatsDefByName(statsDefName);
            if(dstStatsDef != null) {
                LOGGER.info("Removing previous StatsDef " + statsDefName);
                dstGeostore.delete(dstStatsDef.getId());
            }

            LOGGER.info("Copying StatsDef " + statsDefName);
            Resource srcStatsDef = srcGeostore.searchStatsDefByName(statsDefName);
            RESTResource statsDef = FlowUtil.copyResource(srcStatsDef);
            dstGeostore.insert(statsDef);

            //=== OVERWRITE STATSDATA

            Resource dstStatsData = dstGeostore.searchStatsData(statsDefName, year, month);
            if(dstStatsData != null) {
                LOGGER.info("Removing previous StatsData [statsdef:"+statsDefName+" year:"+year+" month:"+month+"]");
                dstGeostore.delete(dstStatsData.getId());
            }

            Resource srcStatsData = srcGeostore.searchStatsData(statsDefName, year, month);
            if (srcStatsData == null) {
                LOGGER.warn("No StatsData found for [statsdef:"+statsDefName+" year:"+year+" month:"+month+"]");
            } else {
                LOGGER.info("Copying StatsData " + srcStatsData.getName());
                RESTResource statsData = FlowUtil.copyResource(srcStatsData);
                dstGeostore.insert(statsData);
            }

            // Collect dependant chartscript to be copied
            List<Resource> csList = srcGeostore.searchChartScriptByStatsDef(statsDefName);
            for (Resource cs : csList) {
                LOGGER.info("Collecting ChartScript: adding " + cs.getName());
                srcChartScripts.put(cs.getId(), cs);
            }
        }

        //==================================================
        //=== CHARTS
        //===
        //=== loop on all the collected charts

        for (Resource srcChartScript : srcChartScripts.values()) {
            String chartScriptName = srcChartScript.getName();
            LOGGER.info("Removing previous charts stuff: " + chartScriptName);

            //== remove chartScript
            Resource dstChartScript = dstGeostore.searchChartScript(chartScriptName);
            if(dstChartScript != null) {
                LOGGER.info("Removing previous ChartScript :"+ chartScriptName);
                dstGeostore.delete(dstChartScript.getId());
            }

            //== remove chartData
            List<ShortResource> dstChartDataList = dstGeostore.searchChartDataByChartScript(chartScriptName);
            for (ShortResource dstChartData : dstChartDataList) {
                LOGGER.info("Removing previous ChartData :"+ dstChartData.getName() + " [cscript:" + chartScriptName+ ']');
                dstGeostore.delete(dstChartData.getId());
            }

            //== Insert chartScript
            LOGGER.info("Copying ChartScript " + chartScriptName);
            RESTResource chartScript = FlowUtil.copyResource(srcChartScript);
            dstGeostore.insert(chartScript);

            //== Insert chartData
            List<Resource> srcChartDataList = srcGeostore.searchChartDataPublished(chartScriptName);
            for (Resource srcChartData : srcChartDataList) {
                LOGGER.info("Copying ChartData " + srcChartData.getName());
                RESTResource chartData = FlowUtil.copyResource(srcChartData);
                dstGeostore.insert(chartData);
            }
        }
    }


    /**
     * Copy the original data dir.
     * Data will be copied
     * from srcRepositoryDirectory/relativepath/snapshotlayer to
     * trgRepositoryDirectory/<relativepath>/<snapshotlayer> where the
     * snapshotlayer is <layer>_<year>_<month>
     *
     * @param srcDir
     * @param dstDir
     * @param relativepath
     * @param layer
     * @param year
     * @param month
     * @throws IOException
     */
    public void copyOriginalData(String srcDir, String dstDir, String relativepath, String layer, String year, String month) throws IOException {
        LOGGER.error("*** TODO: copy original data to dissemination system");

        // refers to srcdir/<relativepath>/<snapshotlayer>
//        String dirname = layer + "_" + year;
//        if (month != null) {
//            dirname += "_" + month;
//        }
//        File srclayerdirname = new File(srcDir, relativepath);
//        File srcsnapshotdirname = new File(srclayerdirname, dirname);
//
//        // build the target directory
//        File trglayerdirname = new File(dstDir, relativepath);
//        File trgsnapshotdirname = new File(trglayerdirname, dirname);
//
//
//        FileUtils.copyDirectory(srcsnapshotdirname, trgsnapshotdirname);

    }

    /************
     * copy the tiff of source mosaic to the target mosaic directory
     * dissemination path
     * @throws IOException
     */
    public void copyRaster(String srcPath, String dstPath, String layer, String year, String month) throws IOException {

        String filename = NameUtils.buildTifFileName(layer, year, month);
        File srcFile = new File(srcPath, filename);
        File dstDir = new File(dstPath);

        FileUtils.copyFileToDirectory(srcFile, dstDir);
        // todo: copy also ancillary files if any (external OV, ...)
    }

    /**
     * copy the snapshot of the mosaic identified by layer, year and
     * month namely it copies both the mosaic directory relative to the snapshot
     * and the entry into the db
     *
     * @param params this are the paramaters to connect to the source postgis
     * database where the mosaic snapshot is stored
     * @param layer
     * @param year
     * @param month
     * @throws IOException
     */
//    public void copyMosaicDBEntry(String layer, String year, String month) throws IOException {
//
//        postGISMosaicTo.copyMosaic(postGISMosaicFrom.getParams(), layer, year, month, 1000, true);
//    }

    public void updatePostGIS(PostGisConfig srcPgCfg, PostGisConfig dstPgCfg, String layer, String year, String month) throws ActionException {
        try {
            LOGGER.info("Removing features from the dissemination PostGIS...");
            PostGISUtils.removeFeatures(dstPgCfg, layer, year, month);
//            postGISTo.removeFeatures(layer, year, month);
            LOGGER.info("Features successfully removed from the dissemination PostGIS");

            LOGGER.info("Copying features ...");
            PostGISUtils.copyFeatures(srcPgCfg, dstPgCfg, layer, year, month, true); // TODO: change to false
            LOGGER.info("Features successfully copied");

        } catch (PostGisException e) {
            throw new ActionException(this, "Error while copying features", e);
        }
    }

}