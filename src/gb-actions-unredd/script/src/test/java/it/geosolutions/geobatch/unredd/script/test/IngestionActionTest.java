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

import static org.junit.Assume.assumeTrue;
import it.geosolutions.geobatch.flow.event.action.ActionException;
import it.geosolutions.geobatch.unredd.script.exception.FlowException;
import it.geosolutions.geobatch.unredd.script.exception.GeoStoreException;
import it.geosolutions.geobatch.unredd.script.ingestion.IngestionAction;
import it.geosolutions.geobatch.unredd.script.ingestion.IngestionConfiguration;
import it.geosolutions.geobatch.unredd.script.test.utils.UNREDDGeoStoreTestUtil;
import it.geosolutions.geobatch.unredd.script.util.SingleFileActionExecutor;
import it.geosolutions.geostore.services.rest.model.RESTResource;
import it.geosolutions.unredd.geostore.model.UNREDDCategories;
import it.geosolutions.unredd.geostore.model.UNREDDFormat;
import it.geosolutions.unredd.geostore.model.UNREDDLayer;
import it.geosolutions.unredd.geostore.model.UNREDDLayerUpdate;
import it.geosolutions.unredd.geostore.model.UNREDDLayerUpdate.Attributes;
import it.geosolutions.unredd.geostore.utils.NameUtils;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IngestionActionTest extends BaseTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(IngestionActionTest.class);

    public IngestionConfiguration ingestionCfg;
    static IngestionAction ingestionAction  = null;
//    static String mosaicdir;

    @BeforeClass
    public static void onceForAll() throws Exception {
//        LOGGER.info("-------------------------> BeforeClass:  Reprocess Action Test <-----------------------------");
    }

    @Before
    public void before() throws Exception {
        assertNotNull(getGeoStoreUtil());
        assumeTrue(pingGeoStore());

        ingestionAction = builAction();
        
        LOGGER.info("Cleaning Staging Area environment");

        UNREDDGeoStoreTestUtil.deleteAllResources();

//        LOGGER.info("-------------------------< End Before:  Reprocess Action Test <-----------------------------");
    }

    protected IngestionAction builAction() throws Exception {
        ingestionCfg = (IngestionConfiguration)loadActionConfig("ingestion/ingestionCfg.xml");
        ingestionCfg.setGeoStoreConfig(geoStoreConfig);
        ingestionCfg.setPostGisConfig(postGisConfig);
        ingestionCfg.setRasterizeConfig(rasterizeConfig);
        ingestionCfg.setConfigDir(loadFile("configuration/rasterize")); // config is only used by freemarker/taskexec

        File origTarget = new File(getTempDir(), "orig");
        origTarget.mkdir();

        IngestionAction a  = new IngestionAction(ingestionCfg);
        a.setTempDir(getTempDir());

        return a;
    }

    @AfterClass
    public static void tearDown() throws Exception {
//        geostoreUtil.delete();
//        if (testContextDir.exists()) FileUtils.deleteDirectory(testContextDir);
//        FileUtils.deleteDirectory(new File(mosaicdir));
    }

    protected void clearPlayfield() throws GeoStoreException {
        
        LOGGER.info("===== Clear playfield");
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.LAYER);
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.LAYERUPDATE);
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.STATSDEF);
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.STATSDATA);
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.CHARTDATA);
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.CHARTSCRIPT);
    }
    
    protected UNREDDLayer buildRasterLayer() {

        LOGGER.info("===== Add sample data: Layer");
        File mosaicDir = new File(getTempDir(), "mosaic");
        mosaicDir.mkdir();

        UNREDDLayer layer = new UNREDDLayer();
        layer.setAttribute(UNREDDLayer.Attributes.LAYERTYPE, UNREDDFormat.RASTER.getName());
        layer.setAttribute(UNREDDLayer.Attributes.MOSAICPATH, mosaicDir.getAbsolutePath());
        //layer.setAttribute(UNREDDLayer.Attributes.DESTORIGRELATIVEPATH, getTempDir().getAbsolutePath());
        layer.setAttribute(UNREDDLayer.Attributes.RASTERPIXELWIDTH,  "3876");
        layer.setAttribute(UNREDDLayer.Attributes.RASTERPIXELHEIGHT, "3562");
        layer.setAttribute(UNREDDLayer.Attributes.RASTERX0, "21");
        layer.setAttribute(UNREDDLayer.Attributes.RASTERX1, "23");
        layer.setAttribute(UNREDDLayer.Attributes.RASTERY0, "-6.5");
        layer.setAttribute(UNREDDLayer.Attributes.RASTERY1, "5.1");

        return layer;
    }

    protected UNREDDLayer buildVectorLayer() {

        LOGGER.info("===== Add sample data: Vector Layer");
        File mosaicDir = new File(getTempDir(), "mosaic");
        mosaicDir.mkdir();

        UNREDDLayer layer = new UNREDDLayer();
        layer.setAttribute(UNREDDLayer.Attributes.LAYERTYPE, UNREDDFormat.VECTOR.getName());
        layer.setAttribute(UNREDDLayer.Attributes.MOSAICPATH, mosaicDir.getAbsolutePath());
        //layer.setAttribute(UNREDDLayer.Attributes.DESTORIGRELATIVEPATH, getTempDir().getAbsolutePath());
        layer.setAttribute(UNREDDLayer.Attributes.RASTERPIXELWIDTH,  "3876");
        layer.setAttribute(UNREDDLayer.Attributes.RASTERPIXELHEIGHT, "3562");
        layer.setAttribute(UNREDDLayer.Attributes.RASTERX0, "21");
        layer.setAttribute(UNREDDLayer.Attributes.RASTERX1, "23");
        layer.setAttribute(UNREDDLayer.Attributes.RASTERY0, "-6.5");
        layer.setAttribute(UNREDDLayer.Attributes.RASTERY1, "5.1");
        layer.setAttribute(UNREDDLayer.Attributes.RASTERATTRIBNAME, "scene_id");
        layer.setAttribute(UNREDDLayer.Attributes.RASTERNODATA,     "999999");

        return layer;
    }

    @Test
    @Ignore
    public void testBadFormat() throws GeoStoreException, FlowException, ActionException {
        clearPlayfield();
        UNREDDLayer layer = buildRasterLayer();
        layer.setAttribute(UNREDDLayer.Attributes.LAYERTYPE, UNREDDFormat.VECTOR.getName());
        RESTResource layerResource = layer.createRESTResource();
        layerResource.setName("layer1");
        gstcu.insert(layerResource);

        //=== Data is set, prepare flow
        LOGGER.info("===== Run the code!");
        File inputFile = loadFile("context/Request.zip");
        try {
            SingleFileActionExecutor.execute(ingestionAction, inputFile);
            fail("Untrapped exception");
        } catch (ActionException x) {
            LOGGER.info("Exception properly trapped");
        }
    }

    @Test
    @Ignore
    public void testBadSize() throws GeoStoreException, FlowException, ActionException {
        clearPlayfield();
        UNREDDLayer layer = buildRasterLayer();
        layer.setAttribute(UNREDDLayer.Attributes.RASTERPIXELWIDTH, "42");
        RESTResource layerResource = layer.createRESTResource();
        layerResource.setName("layer1");
        gstcu.insert(layerResource);

        //=== Data is set, prepare flow
        LOGGER.info("===== Run the code!");
        File inputFile = loadFile("context/Request.zip");
        try {
            SingleFileActionExecutor.execute(ingestionAction, inputFile);
            fail("Untrapped exception");
        } catch (ActionException x) {
            LOGGER.info("Exception properly trapped");
        }
    }

    @Test
    @Ignore
    public void testBadExistingLayerUpdate() throws GeoStoreException, FlowException, ActionException {
        clearPlayfield();
        UNREDDLayer layer = buildRasterLayer();
        RESTResource layerResource = layer.createRESTResource();
        layerResource.setName("layer1");
        gstcu.insert(layerResource);

        UNREDDLayerUpdate layerUpdate = new UNREDDLayerUpdate();
        layerUpdate.setAttribute(Attributes.YEAR, "2012");
        layerUpdate.setAttribute(Attributes.LAYER, "layer1");
        RESTResource luResource = layerUpdate.createRESTResource();
        luResource.setName(NameUtils.buildLayerUpdateName("layer1", "2012", null, null));
        gstcu.insert(luResource);

        //=== Data is set, prepare flow
        LOGGER.info("===== Run the code!");
        File inputFile = loadFile("context/Request.zip");
        try {
            SingleFileActionExecutor.execute(ingestionAction, inputFile);
            fail("Untrapped exception");
        } catch (ActionException x) {
            LOGGER.info("Exception properly trapped");
        }
    }

    @Test
    public void testProperIngestRaster() throws GeoStoreException, FlowException, ActionException {

        clearPlayfield();
        UNREDDLayer layer = buildRasterLayer();
        RESTResource layerResource = layer.createRESTResource();
        layerResource.setName("layer1");
        gstcu.insert(layerResource);
        
        //=== Data is set, prepare flow
        LOGGER.info("===== Run the code!");
        File inputFile = loadFile("context/Request.zip");
        SingleFileActionExecutor.execute(ingestionAction, inputFile);

        //=== Check outcome
        LOGGER.info("===== Testing results");

        File mosaicDir = new File(getTempDir(), "mosaic");
        File finalFile = new File(mosaicDir, NameUtils.buildTifFileName("layer1", "2012", null, null));
        assertTrue(finalFile.exists());
    }


    @Test
    public void testProperIngestVector() throws GeoStoreException, FlowException, ActionException {

        clearPlayfield();
        UNREDDLayer layer = buildVectorLayer();
        RESTResource layerResource = layer.createRESTResource();
        layerResource.setName("layer1");
        gstcu.insert(layerResource);

        //=== Data is set, prepare flow
        LOGGER.info("===== Run the code!");
        File inputFile = loadFile("context/RequestSHP.zip");
        SingleFileActionExecutor.execute(ingestionAction, inputFile);

        //=== Check outcome
        LOGGER.info("===== Testing results");

        File mosaicDir = new File(getTempDir(), "mosaic");
        File finalFile = new File(mosaicDir, NameUtils.buildTifFileName("layer1", "2010", "10", "07")); // these values are in the info.xml file
        assertTrue(finalFile.exists());
    }

   
}
