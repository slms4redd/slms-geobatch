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

import it.geosolutions.geobatch.unredd.script.exception.FlowException;
import it.geosolutions.geobatch.unredd.script.exception.GeoStoreException;
import it.geosolutions.geobatch.unredd.script.test.utils.UNREDDGeoStoreTestUtil;
import it.geosolutions.geobatch.unredd.script.util.FlowUtil;
import it.geosolutions.geobatch.unredd.script.util.Statistics.Tokens;
import it.geosolutions.geostore.core.model.Resource;
import it.geosolutions.geostore.services.dto.search.CategoryFilter;
import it.geosolutions.geostore.services.dto.search.SearchFilter;
import it.geosolutions.geostore.services.dto.search.SearchOperator;
import it.geosolutions.geostore.services.rest.model.RESTCategory;
import it.geosolutions.geostore.services.rest.model.RESTResource;
import it.geosolutions.geostore.services.rest.model.ShortResourceList;
import it.geosolutions.unredd.geostore.model.UNREDDCategories;
import it.geosolutions.unredd.geostore.model.UNREDDChartData;
import it.geosolutions.unredd.geostore.model.UNREDDChartScript;
import it.geosolutions.unredd.geostore.model.UNREDDChartScript.Attributes;
import it.geosolutions.unredd.geostore.model.UNREDDChartScript.ReverseAttributes;
import it.geosolutions.unredd.geostore.utils.NameUtils;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlowUtilsTest extends BaseTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(FlowUtilsTest.class);

    @BeforeClass
    public static void runOnce() throws IOException {
    }

    @Before
    public void setUp() throws Exception {
    }

    /**
     * Needs a running geostore for testing
     */
    @Test
//    @Ignore
    public void testRunScripts() throws GeoStoreException, IOException, FlowException {
        assertNotNull(getGeoStoreUtil());
        assumeTrue(pingGeoStore());

        // some test data
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.STATSDATA);

        UNREDDGeoStoreTestUtil.insertStatsData("sdata1", "2010", "1", null, "data01");
        UNREDDGeoStoreTestUtil.insertStatsData("sdata1", "2010", "2", null, "data02");
        UNREDDGeoStoreTestUtil.insertStatsData("sdata1", "2010", "3", null, "data03");
        UNREDDGeoStoreTestUtil.insertStatsData("sdata2", "2000", null, null,"data04");
        UNREDDGeoStoreTestUtil.insertStatsData("sdata2", "2010", null, null,"data05");

        // clear playfield
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.CHARTDATA);
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.CHARTSCRIPT);


        // add a chartscript as a test data
        File testScript = loadFile("configuration/script/testScript.groovy");
        LOGGER.info("Test groovy script is " + testScript);

        UNREDDChartScript chartScript = new UNREDDChartScript();
        chartScript.setAttribute(Attributes.SCRIPTPATH, testScript.getAbsolutePath());
        chartScript.addReverseAttribute(ReverseAttributes.STATSDEF, "area_admin1_fc13"); // any one is good

        RESTResource chartScriptRes = chartScript.createRESTResource();
        chartScriptRes.setName("testChartScript");
//        long chartScriptId = geostoreClient.insert(chartScriptRes);
        long chartScriptId = gstcu.insert(chartScriptRes);
//        Resource fullChartScript = geostoreClient.getResource(chartScriptId);
        Resource fullChartScript = gstcu.getGeoStoreClient().getResource(chartScriptId);

        // ok, some real testing now
        SearchFilter chartDataFilter = new CategoryFilter(UNREDDCategories.CHARTDATA.getName(), SearchOperator.EQUAL_TO);
//        ShortResourceList res0 = geostoreClient.searchResources(chartDataFilter);
        ShortResourceList res0 = gstcu.getGeoStoreClient().searchResources(chartDataFilter);
        assertTrue(res0.isEmpty());


        FlowUtil flowUtil = new FlowUtil(getTempDir(), getConfigDir());
        flowUtil.runScripts(getGeoStoreUtil(), Arrays.asList(fullChartScript));

        ShortResourceList res1 = gstcu.getGeoStoreClient().searchResources(chartDataFilter);
        assertEquals(5, res1.getList().size());
    }

    /**
     */
    @Test
    public void testProcessStatistics() throws GeoStoreException, FlowException {

        assertNotNull(getGeoStoreUtil());
        assumeTrue(pingGeoStore());

        // clear playfield
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.STATSDATA);
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.CHARTDATA);
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.CHARTSCRIPT);

        File dataFile = loadFile("reprocess/stats/rdc_area.tif");
        assertNotNull(dataFile);
        File classFile = loadFile("reprocess/stats/rdc_small_2010.tif");
        assertNotNull(classFile);

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

        final String STATSDEFNAME = "testStatsDef";
        final String YEAR = "1999";
        final String MONTH = "12";
        final String DAY = "07";

        Long sdId = UNREDDGeoStoreTestUtil.insertStatsDef(STATSDEFNAME, smallStat, "layerName");
        Resource statsDef = gstcu.getFullResource(sdId);

        String rasterfile = classFile.getAbsolutePath();

        Map<Tokens,String> tok = FlowUtil.fillTokens(rasterfile, null, null, null, null);

        FlowUtil flowUtil = new FlowUtil(getTempDir(), getConfigDir());

        // repeat run twice:
        // first time stats will be INSERTed
        // second time stats will be UPDATEd
        for (int i = 0; i < 2; i++) {
            String statsOut = flowUtil.processStatistics(getGeoStoreUtil(), statsDef, YEAR, MONTH, DAY, tok);
    //        LOGGER.info(statsOut);
            long outlen = statsOut.length();

            assertTrue("Empty output", outlen>0);

            Resource loadedStatsData = getGeoStoreUtil().searchStatsData(STATSDEFNAME, YEAR, MONTH, DAY);
            assertNotNull("Could not reload StatsData", loadedStatsData);
            assertEquals("Stats len mismatch", outlen, loadedStatsData.getData().getData().length());
        }
    }

    /**
     * TODO!!!
     */
    @Test
    public void testRunStatsAndScripts() throws GeoStoreException, FlowException {
        assertNotNull(getGeoStoreUtil());
        assumeTrue(pingGeoStore());

        //--------------------
        // clear playfield
        LOGGER.info("===== Clear playfield");
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.STATSDEF);
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.STATSDATA);
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.CHARTDATA);
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.CHARTSCRIPT);

        //--------------------
        LOGGER.info("===== Add sample data: stats");
        File dataFile = loadFile("reprocess/stats/rdc_area.tif");
        assertNotNull(dataFile);
        File classFile = loadFile("reprocess/stats/rdc_small_2010.tif");
        assertNotNull(classFile);

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


        final String LAYERNAME    = "layerName";
        final String STATSDEFNAME = "testStatsDef";
        final String YEAR = "1999";
        final String MONTH = "12";
        final String DAY = "07";

        UNREDDGeoStoreTestUtil.insertStatsDef(STATSDEFNAME, smallStat, LAYERNAME);

        //--------------------
        LOGGER.info("===== Add sample data: chartScript");

        // add a chartscript as a test data
        File testScript = loadFile("configuration/script/testScript.groovy");
        LOGGER.info("Test groovy script is " + testScript);

        UNREDDChartScript chartScript = new UNREDDChartScript();
        chartScript.setAttribute(Attributes.SCRIPTPATH, testScript.getAbsolutePath());
        chartScript.addReverseAttribute(ReverseAttributes.STATSDEF, STATSDEFNAME); 

        RESTResource chartScriptRes = chartScript.createRESTResource();
        chartScriptRes.setName("testChartScript");
        gstcu.insert(chartScriptRes);
//        Resource fullChartScript = gstcu.getGeoStoreClient().getResource(chartScriptId);

        SearchFilter chartDataFilter = new CategoryFilter(UNREDDCategories.CHARTDATA.getName(), SearchOperator.EQUAL_TO);
        ShortResourceList res0 = gstcu.getGeoStoreClient().searchResources(chartDataFilter);
        assertTrue(res0.isEmpty());

        //---------------------
        // ok, some real testing now

        LOGGER.info("===== Run the code!");

        FlowUtil flowUtil = new FlowUtil(getTempDir(), getConfigDir());
        flowUtil.runStatsAndScripts(LAYERNAME, YEAR, MONTH, DAY, classFile, getGeoStoreUtil());

//---------------------
        LOGGER.info("===== Testing results");

        Resource loadedStatsData = gstcu.searchStatsData(STATSDEFNAME, YEAR, MONTH, DAY);
        assertNotNull("Could not reload StatsData", loadedStatsData);

        ShortResourceList res1 = gstcu.getGeoStoreClient().searchResources(chartDataFilter);
        assertNotNull(res1);
        assertNotNull(res1.getList());
        assertEquals(1, res1.getList().size());

    }

}
