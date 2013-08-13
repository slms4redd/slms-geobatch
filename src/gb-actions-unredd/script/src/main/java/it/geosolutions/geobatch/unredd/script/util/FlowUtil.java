/*
 *  Copyright (C) 2007 - 2012 GeoSolutions S.A.S.
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
package it.geosolutions.geobatch.unredd.script.util;

import it.geosolutions.geobatch.action.scripting.ScriptingAction;
import it.geosolutions.geobatch.action.scripting.ScriptingConfiguration;
import it.geosolutions.geobatch.flow.event.action.ActionException;
import it.geosolutions.geobatch.unredd.script.exception.FlowException;
import it.geosolutions.geobatch.unredd.script.exception.GeoStoreException;
import it.geosolutions.geobatch.unredd.script.util.Statistics.Tokens;
import it.geosolutions.geostore.core.model.Attribute;
import it.geosolutions.geostore.core.model.Resource;
import it.geosolutions.geostore.services.dto.ShortAttribute;
import it.geosolutions.geostore.services.rest.model.RESTCategory;
import it.geosolutions.geostore.services.rest.model.RESTResource;
import it.geosolutions.unredd.geostore.model.UNREDDChartScript;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Procedures used in Ingestion, Reprocess and Publish flows.
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class FlowUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(FlowUtil.class);

    private final File tempDir;
    private final File configDir;

    public FlowUtil(File tempDir, File configDir) {
        this.tempDir = tempDir;
        this.configDir = configDir;
    }


//    /**
//     * This is a wrapper to a NameUtils method.
//     *
//     * @deprecated Use {@link NameUtils} directly.
//     */
//    public static String buildLayerUpdateName(String layerName, String year, String month) {
//        return NameUtils.buildLayerUpdateName(layerName, year, month);
//    }
//
//    /**
//     * This is a wrapper to a NameUtils method.
//     *
//     * @deprecated Use {@link NameUtils} directly.
//     */
//    public static String buildStatsDataName(String statsDefName, String year, String month) {
//        return NameUtils.buildStatsDataName(statsDefName, year, month);
//    }
//
//    /**
//     * Build the filename for the tif file.
//     *
//     * This is a wrapper to a NameUtils method.
//     *
//     * @deprecated Use {@link NameUtils} directly.
//     */
//    public static String buildTifFileName(String layerName, String year, String month) {
//        return NameUtils.buildTifFileName(layerName, year, month);
//    }

    /**
     * Copy a Resource into a RESTResource.
     * May be useful when copying a Resource between two GeoStoreUtil instances.
     *
     * @param resource the source Resource
     * @return
     */
    public static RESTResource copyResource(Resource resource) {
        RESTResource ret = new RESTResource();
        ret.setName(resource.getName());
        ret.setDescription(resource.getDescription());
        ret.setMetadata(resource.getMetadata());
        if(resource.getData() != null)
            ret.setData(resource.getData().getData());
        ret.setCategory(new RESTCategory(resource.getCategory().getName()));
        copyResourceAttribs(resource, ret);
        return ret;
    }

    protected static void copyResourceAttribs(Resource resource, RESTResource rr) {
        List<ShortAttribute> list = new ArrayList<ShortAttribute>();
        for (Attribute attribute : resource.getAttribute()) {
            ShortAttribute shatt = new ShortAttribute(attribute);
            list.add(shatt);
        }
        rr.setAttribute(list);
    }


    /**
     * Put some required tokens in the props map.<br/>
     * If orig map is null, it will be instantiated.<br/>
     * Null values will not be put into the map.
     * 
     * @return
     */
    public static Map<Statistics.Tokens, String> fillTokens(String rasterFullPath, String layerName, String year, String month,
            Map<Statistics.Tokens, String> props) {
        
        if (props == null) {
            props = new EnumMap(Statistics.Tokens.class);
        }

        if(rasterFullPath != null)
            props.put(Tokens.FILEPATH, rasterFullPath);
        if(layerName != null)
            props.put(Tokens.LAYERNAME, layerName);
        if(year != null)
            props.put(Tokens.YEAR, year);        
        if (month != null) 
            props.put(Tokens.MONTH, month);

        return props;
    }

    /**
     * Run statistics and store(insert/replace) computed StatsData.
     *
     * @param geostoreUtil The target GeoStore for storing computed StatsData
     * @param statsDef The Resource holding the stats definition
     * @param tokens The tokens to be replaced in the stats definition; see also the facility {@link #fillTokens(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Map) fillTokens(...)}
     * @param year Used as metadata in new StatsData entry
     * @param month Used as metadata in new StatsData entry
     * 
     * @return the stats output (usually a csv)
     * 
     * @throws GeoStoreException
     * @throws FlowException 
     */
    public String processStatistics(GeoStoreUtil geostoreUtil, Resource statsDef, String year, String month, String day, Map<Statistics.Tokens, String> tokens)
            throws FlowException {

        if(tokens == null)
            throw new FlowException("Tokens are null");
        if(statsDef == null)
            throw new FlowException("StatsDef is null");
        if(statsDef.getData() == null)
            throw new FlowException("StoredData is null");
        if(statsDef.getData().getData() == null)
            throw new FlowException("StoredData has no content");

        LOGGER.info("Preparing to run statistics " + statsDef.getName() + " on " + tokens.get(Statistics.Tokens.FILEPATH));

        File outStats = null;

        // execute the statistics statdefres.getData().getData() substituting variable properties by those indicated in tokenProps
        // and save the result on a file named outFileName

        try {
            outStats = File.createTempFile("statsresults", ".csv", tempDir);

            Statistics statistics = new Statistics();
            String statsDefinition = statsDef.getData().getData();
            statistics.executeStatistics(statsDefinition, tokens, outStats.getAbsolutePath());
            LOGGER.info("Statistics processed: " + statsDef.getName());
            
            String statsContent = IOUtils.toString(new FileReader(outStats));
//            LOGGER.info("Statistics result :" + statsContent);

            geostoreUtil.setStatsData(statsDef, statsContent, year, month, day);
            return statsContent;

        } catch (Exception ex) {
            LOGGER.debug("ex!", ex);
            throw new FlowException("Error while executing stats: " + ex.getMessage(), ex);
//        } finally {
//            FileUtils.deleteQuietly(outStats);
        } catch (Error ex) { // just log it
            LOGGER.error("ERROR", ex);
            throw ex;
//        } finally {
//            FileUtils.deleteQuietly(outStats);
        }

    }


    public void runScripts(GeoStoreUtil geoStore, Collection<Resource> chartScriptList) throws Exception {
        LOGGER.info("Running " + chartScriptList.size() + " scripts...");
        String currentScript = null;
        try {
            Map props = new HashMap();
            props.put("geostore_url", geoStore.getConfigUrl());
            props.put("geostore_username", geoStore.getConfigUsername());
            props.put("geostore_password", geoStore.getConfigPassword());


            for (Resource scriptResource : chartScriptList) {
                currentScript = scriptResource.getName() + "(id:"+scriptResource.getId()+")";
                LOGGER.info("Running " + currentScript);
                runScript(scriptResource, props);
            }
        } catch (ActionException e) {
            throw new FlowException("Error while running Chart Script " + currentScript, e);
        }
    }

    protected void runScript(Resource chartScriptResource, Map<String, Object> properties) throws Exception {

        UNREDDChartScript chartScript = new UNREDDChartScript(chartScriptResource);
        String scriptPath = chartScript.getAttribute(UNREDDChartScript.Attributes.SCRIPTPATH);

        LOGGER.info("Running script '"+chartScriptResource.getName()+"' @ " + scriptPath);

        if(properties == null)
            properties = new HashMap();

        properties.put("chartscript_name", chartScriptResource.getName());
        properties.put("script_path", scriptPath);

        if (scriptPath == null) {
            LOGGER.error("Script '"+chartScriptResource.getName()+"' has a null path");
            return;
        }

   	 	ScriptingConfiguration scriptConf = new ScriptingConfiguration("id","Script: " + chartScriptResource.getName(), "Descr: " + chartScriptResource.getDescription());
//   	 	scriptConf.setWorkingDirectory(tempDir.getAbsolutePath()); // TODO checkme
   	 	scriptConf.setLanguage("groovy");
   	 	scriptConf.setScriptFile(scriptPath);
 	 	scriptConf.setProperties(properties);

//        ScriptingService scriptService = new ScriptingService("scriptId","scriptName","scriptDescr");
//   	 	if (!scriptService.canCreateAction(scriptConf))
//            throw new IllegalArgumentException("The arguments for the ScriptingAction are not complete or illegal");

        ScriptingAction scriptAction = new ScriptingAction(scriptConf);
        scriptAction.setTempDir(tempDir);
        scriptAction.setConfigDir(configDir);
        
        
        SingleFileActionExecutor.executeMultiReturn(scriptAction, null);
    }


    public void runStatsAndScripts(String layername, String year, String month, String day, File rasterFile, GeoStoreUtil geostore) throws FlowException {

        // ********************
        // Retrieve stats to run
        //
        // ********************
        List<Resource> relatedStatsDef = null;
        try {
            relatedStatsDef = geostore.searchStatsDefByLayer(layername, false);
        } catch (Exception e) {
            LOGGER.debug("Parameter : [layername=" + layername + ", year=" + year + ", month=" + month + "]");
            throw new FlowException("Error while searching for StatsDef", e);
        }

        // ********************
        // Run statistics
        // --------------------
        // For each statsdef performs the statistics and collect the related chartScript.
        // This way, needed ChartScript will be run once even if related to more than one stat.
        // ********************

        Set<Resource> chartScript = new HashSet<Resource>();

        try {
            for (Resource statsDef : relatedStatsDef) {
                Map<Tokens,String> tok = fillTokens(rasterFile.getAbsolutePath(), layername, year, month, null);
                processStatistics(geostore, statsDef, year, month, day, tok);

                List<Resource> localChartScript = geostore.searchChartScriptByStatsDef(statsDef.getName());
                if(LOGGER.isInfoEnabled())
                    LOGGER.info("Found " + localChartScript.size() + " ChartsScript depending on StatsDef '"+statsDef.getName()+"'");

                chartScript.addAll(localChartScript);
            }
        } catch (Exception e) {
            throw new FlowException("Error while running stats", e);
        }

        // ********************
        // Run scripts
        // ********************

        try {
            runScripts(geostore, chartScript);
        } catch (Exception e) {
            throw new FlowException("Error while running scripts", e);
        }

    }
}
