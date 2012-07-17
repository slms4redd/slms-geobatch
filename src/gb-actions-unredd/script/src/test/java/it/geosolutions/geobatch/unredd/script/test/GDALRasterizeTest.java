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

import it.geosolutions.geobatch.unredd.script.model.RasterizeConfig;

import java.io.File;
import it.geosolutions.geobatch.unredd.script.util.rasterize.GDALRasterize;
import it.geosolutions.unredd.geostore.model.UNREDDLayer;
import it.geosolutions.unredd.geostore.model.UNREDDLayer.Attributes;
import it.geosolutions.unredd.geostore.model.UNREDDLayerUpdate;

import static org.junit.Assert.*;
import static org.junit.Assume.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GDALRasterizeTest extends BaseTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(GDALRasterizeTest.class);

//    private static Properties loadedProps;

//    private final File configDir = loadFile("configuration/");
            //new File("src/test/resources/configuration");

//    private static PostGIS pgSA;
//    private GDALOptions options;
//    private static String gdalpath;
    
    
    
    @BeforeClass
    public static void mainSetUp() throws Exception {
//        loadedProps = PropertyLoaderTestUtil.load("testdata.properties");
//        gdalpath = loadedProps.getProperty("gdal.path");

    }

    @Before
    public void setUp() throws Exception {
    
//        LOGGER.info("Config dir is " + getconfigDir);

//        tempDir = tempFolder.newFolder(name.getMethodName());
        // using next two lines in order not to delete automatically the created file to be able to inspect them
//        tempDir = new File("/tmp", ""+System.currentTimeMillis());
//        tempDir.mkdir();
//        LOGGER.info("Using temp dir " + tempDir + " for test " + name.getMethodName());
        
        
     // get feature results
//        Map properties = PropertyLoaderTestUtil.loadIntoConf(loadedProps);       
//        URL shapeURL = new URL("file:src/test/resources/layer1_2012.shp");
//        ShapefileDataStore store = new ShapefileDataStore(shapeURL);
//        String name = store.getTypeNames()[0];
//        SimpleFeatureCollection source = store.getFeatureSource(name).getFeatures();
//
//        pgSA = new PostGIS(
//                loadedProps.getProperty("postgisSA.url"),
//                5432,
//                loadedProps.getProperty("postgisSA.db"),
//                loadedProps.getProperty("postgisSA.schema"),
//                loadedProps.getProperty("postgisSA.user"),
//                loadedProps.getProperty("postgisSA.pwd"));
//
//
//
//        // insert new features in the postgis database
//
//
//        pgSA.addFeatures("layer1", source, 2012, 11, 50, true);
//        File configDir = new File("src/test/resources/configuration/rasterize");
//        File srcDir = new File("src/test/resources/configuration/rasterize");
//        File destDir = new File(workingdir, "rasterize");
//        FileUtils.copyDirectory(srcDir, destDir);
     }

//    @AfterClass
//    public static void tearDown() throws Exception {
//        pgSA.getPgDatastore().dispose();
////        FileUtils.forceDelete(workingDirFile);
//    }

    @Test
    public void testFromShape() throws Exception {

        File rasterizeConfigDir = loadFile("configuration/rasterize");

//        RasterizeConfig config = new RasterizeConfig();
//        config.setExecutable(gdalpath);
//        config.setFreeMarkerTemplate("commongdalrasterizeshp.xml");
//        config.setTaskExecutorXslFileName("commongdalrasterize.xsl");

        //===
        UNREDDLayerUpdate ulu = new UNREDDLayerUpdate();
        ulu.setAttribute(it.geosolutions.unredd.geostore.model.UNREDDLayerUpdate.Attributes.LAYER, "layer1_2012");
        ulu.setAttribute(it.geosolutions.unredd.geostore.model.UNREDDLayerUpdate.Attributes.YEAR, "2012");

        UNREDDLayer ul = new UNREDDLayer();
        ul.setAttribute(Attributes.RASTERATTRIBNAME, "scene_id");
        ul.setAttribute(Attributes.RASTERNODATA, "0.0");
        ul.setAttribute(Attributes.RASTERX0, "21");
        ul.setAttribute(Attributes.RASTERY0, "-6.5");
        ul.setAttribute(Attributes.RASTERX1, "23");
        ul.setAttribute(Attributes.RASTERY1, "5.1");
        ul.setAttribute(Attributes.RASTERPIXELHEIGHT, "3562");
        ul.setAttribute(Attributes.RASTERPIXELWIDTH, "3876");
        ul.setAttribute(Attributes.RASTERDATATYPE, "byte");


        GDALRasterize rasterize = new GDALRasterize( rasterizeConfig, rasterizeConfigDir, getTempDir());
//        File errorFile = File.createTempFile("error", ".xml", getTempDir());

        File shp = loadFile("shp/layer1/layer1_2012.shp");
        String shpPath = shp.getAbsolutePath();
        LOGGER.info("ShapeFile path is " + shpPath);
//        options.setSrc(shpPath);

        File outfile = new File(getTempDir(), "output.tif");
        LOGGER.info("Output tif path is " + outfile);

        File outputFile = null;
        try {
            outputFile = rasterize.run(ul, ulu, shp);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }

        assertTrue(outputFile.exists());
        long len = outputFile.length();

        LOGGER.info("Output file " + outputFile + " has length " + len);
        assertTrue(len > 0);
    }

    @Test
    public void testFromDB() throws Exception {
        assumeTrue(false);

    }

}
