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

package it.geosolutions.geobatch.unredd.script.test;

import it.geosolutions.geobatch.flow.event.action.ActionException;
import it.geosolutions.geobatch.unredd.script.exception.FlowException;
import it.geosolutions.geobatch.unredd.script.exception.GeoStoreException;
import it.geosolutions.geobatch.unredd.script.reprocess.ReprocessAction;
import it.geosolutions.geobatch.unredd.script.reprocess.ReprocessConfiguration;
import it.geosolutions.geobatch.unredd.script.test.utils.UNREDDGeoStoreTestUtil;
import it.geosolutions.geobatch.unredd.script.util.PostGISUtils;
import it.geosolutions.geobatch.unredd.script.util.SingleFileActionExecutor;
import it.geosolutions.geobatch.unredd.script.util.Statistics.Tokens;
import it.geosolutions.geostore.services.dto.search.CategoryFilter;
import it.geosolutions.geostore.services.dto.search.SearchFilter;
import it.geosolutions.geostore.services.dto.search.SearchOperator;
import it.geosolutions.geostore.services.rest.model.RESTResource;
import it.geosolutions.geostore.services.rest.model.ShortResourceList;
import it.geosolutions.unredd.geostore.model.UNREDDCategories;
import it.geosolutions.unredd.geostore.model.UNREDDChartScript;
import it.geosolutions.unredd.geostore.model.UNREDDChartScript.Attributes;
import it.geosolutions.unredd.geostore.model.UNREDDChartScript.ReverseAttributes;
import it.geosolutions.unredd.geostore.model.UNREDDLayer;

import java.io.File;
import java.io.IOException;
import java.util.Collection;


import org.junit.AfterClass;
import static org.junit.Assert.*;
import static org.junit.Assume.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReprocessActionTest extends BaseTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(ReprocessActionTest.class);

    public ReprocessConfiguration reprocessCfg;
    static ReprocessAction reprocessAction  = null;
    static String mosaicdir;

    @BeforeClass
    public static void onceForAll() throws Exception {
//        LOGGER.info("-------------------------> BeforeClass:  Reprocess Action Test <-----------------------------");
    }

    @Before
    public void before() throws Exception {

        reprocessAction = builReprocessAction();
        
        LOGGER.info("Cleaning Staging Area environment");
        
        UNREDDGeoStoreTestUtil.deleteAllResources();

//        LOGGER.info("-------------------------< End Before:  Reprocess Action Test <-----------------------------");
    }

    protected ReprocessAction builReprocessAction() {
        reprocessCfg = new ReprocessConfiguration("id","name","description");
        reprocessCfg.setGeoStoreConfig(geoStoreConfig);

        ReprocessAction a  = new ReprocessAction(reprocessCfg);
        a.setTempDir(getTempDir());
        a.setConfigDir(getConfigDir());
        return a;
    }

    @AfterClass
    public static void tearDown() throws Exception {
//        geostoreUtil.delete();
//        if (testContextDir.exists()) FileUtils.deleteDirectory(testContextDir);
//        FileUtils.deleteDirectory(new File(mosaicdir));
    }
    

    @Test
    public void testReprocessCharts() throws GeoStoreException, IOException, ActionException {
        assumeTrue(pingGeoStore());

        // some test data
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.STATSDATA);

        UNREDDGeoStoreTestUtil.insertStatsData("sdata1", "2010", "1", "data01");
        UNREDDGeoStoreTestUtil.insertStatsData("sdata1", "2010", "2", "data02");
        UNREDDGeoStoreTestUtil.insertStatsData("sdata1", "2010", "3", "data03");
        UNREDDGeoStoreTestUtil.insertStatsData("sdata2", "2000", null, "data04");
        UNREDDGeoStoreTestUtil.insertStatsData("sdata2", "2010", null, "data05");

        // clear playfield
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.CHARTDATA);
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.CHARTSCRIPT);


        // add a chartscript as a test data
        {
            File testScript = loadFile("configuration/script/testScript.groovy");
            LOGGER.info("Test groovy script is " + testScript);

            UNREDDChartScript chartScript = new UNREDDChartScript();
            chartScript.setAttribute(Attributes.SCRIPTPATH, testScript.getAbsolutePath());
            chartScript.addReverseAttribute(ReverseAttributes.STATSDEF, "area_admin1_fc13"); // any one is good

            RESTResource chartScriptRes = chartScript.createRESTResource();
            chartScriptRes.setName("testChartScript");
            gstcu.insert(chartScriptRes);
        }

//        Resource fullChartScript = gstcu.getGeoStoreClient().getResource(chartScriptId);

        // ok, some real testing now
        SearchFilter chartDataFilter = new CategoryFilter(UNREDDCategories.CHARTDATA.getName(), SearchOperator.EQUAL_TO);

        ShortResourceList res0 = gstcu.getGeoStoreClient().searchResources(chartDataFilter);
        assertTrue(res0.isEmpty());

        LOGGER.info("--------------- Data setup complete-- running real test ----------");
        //=== Data is set, prepare flow
        File inputFile = loadFile("reprocess/reprocessChart.xml");
        SingleFileActionExecutor.execute(reprocessAction, inputFile);
        
        ShortResourceList res1 = gstcu.getGeoStoreClient().searchResources(chartDataFilter);
        assertEquals(5, res1.getList().size());
    }


    @Test
    public void testReprocessStats() throws GeoStoreException, FlowException, ActionException {

        assertNotNull(getGeoStoreUtil());
        assumeTrue(pingGeoStore());

        // clear playfield
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.LAYER);
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.LAYERUPDATE);
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.STATSDEF);
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.STATSDATA);
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.CHARTDATA);
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.CHARTSCRIPT);

        final String LAYERNAME = "rdc_small";
        final String STATSDEFNAME = "testStatsDef";

        File classFile = loadFile("reprocess/stats/rdc_small_2010.tif");
        assertTrue("Error in datafile " + classFile, classFile!= null && classFile.exists());

        // add a layer
        {
            String mosaicDir = classFile.getAbsoluteFile().getParent();

            UNREDDLayer layer = new UNREDDLayer();
            layer.setAttribute(UNREDDLayer.Attributes.MOSAICPATH, mosaicDir);
            RESTResource layerRes = layer.createRESTResource();
            layerRes.setName(LAYERNAME);
            gstcu.insert(layerRes);
        }

        // add a layerUpdate
        {
            gstcu.insertLayerUpdate(LAYERNAME, "2010", null);
        }

        // add a statsDef
        {
            File dataFile = loadFile("reprocess/stats/rdc_area.tif");
            assertNotNull(dataFile);

            String smallStat = " <statisticConfiguration>"
                            + "   <name>area_admin_small</name>"
                            + "   <title>Low resolution admin areas</title>"
                            + "   <description>Compute the area for the administrative areas. Low resolutions raster.</description>"
                            + "   <stats>"
                            + "      <stat>SUM</stat>"
                            + "      <stat>MIN</stat>"
                            + "      <stat>MAX</stat>"
                            + "      <stat>COUNT</stat>"
                            + "   </stats>"
                            + "   <deferredMode>true</deferredMode>"
                            + "   <dataLayer>"
                            + "      <file>"+dataFile.getAbsolutePath()+"</file>" // in production, this will be a fixed string as here
                            + "   </dataLayer>"
                            + "   <classificationLayer zonal=\"true\">"
                            + "      <file>{"+Tokens.FILEPATH+"}</file>"          // in production, this will be expanded as a token as in here
                            + "      <nodata>65535</nodata>"
                            + "   </classificationLayer>"
                            + "   <output>"
                            + "      <format>CSV</format>"
                            + "      <separator>;</separator>"
                            + "      <NaNValue>-1</NaNValue>"
                            + "   </output>"
                            + "</statisticConfiguration>";

            UNREDDGeoStoreTestUtil.insertStatsDef(STATSDEFNAME, smallStat, LAYERNAME);
//            Resource statsDef = gstcu.getFullResource(sdId);
        }

        // insert chartScript
        {
            File testScript = loadFile("configuration/script/testScript.groovy");
            LOGGER.info("Test groovy script is " + testScript);

            UNREDDChartScript chartScript = new UNREDDChartScript();
            chartScript.setAttribute(Attributes.SCRIPTPATH, testScript.getAbsolutePath());
            chartScript.addReverseAttribute(ReverseAttributes.STATSDEF, STATSDEFNAME);

            RESTResource chartScriptRes = chartScript.createRESTResource();
            chartScriptRes.setName("testChartScript");
            gstcu.insert(chartScriptRes);
        }

        LOGGER.info("--------------- Data setup complete-- running real test ----------");
        
        //=== Data is set, prepare flow
        File inputFile = loadFile("reprocess/reprocessStats.xml");
        SingleFileActionExecutor.execute(reprocessAction, inputFile);

        //=== Check outcome
        SearchFilter chartDataFilter = new CategoryFilter(UNREDDCategories.CHARTDATA.getName(), SearchOperator.EQUAL_TO);
        SearchFilter layerUpdateFilter = new CategoryFilter(UNREDDCategories.LAYERUPDATE.getName(), SearchOperator.EQUAL_TO);

        ShortResourceList luList = gstcu.getGeoStoreClient().searchResources(layerUpdateFilter);
        assertEquals("Bad layerupdate #", 1, luList.getList().size());
        assertEquals(LAYERNAME+"_2010", luList.getList().get(0).getName());

        ShortResourceList cdList = gstcu.getGeoStoreClient().searchResources(chartDataFilter);
        assertEquals("Bad layerupdate #", 1, cdList.getList().size());
    }

    
    @Test
    @Ignore
    public void testReprocessLayer() throws GeoStoreException, ActionException  {

        assumeTrue(pingGeoStore());
//        PostGISUtils pg = PostGISUtils.createInstance(postGisConfig);
//        assertNotNull(pg);






        
    }

}
