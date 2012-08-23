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

import static org.junit.Assert.assertTrue;
import it.geosolutions.geobatch.unredd.script.exception.PostGisException;
import it.geosolutions.geobatch.unredd.script.model.PostGisConfig;
import it.geosolutions.geobatch.unredd.script.test.BaseTest;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.geotools.data.DataStore;
import org.geotools.data.Transaction;
import org.geotools.jdbc.JDBCDataStore;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class MosaicTest extends BaseTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(MosaicTest.class);


    public MosaicTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testMosaic() throws Exception {
    	
    	// NB These are local directory
    	File mosaicDir = new File("C:\\Users\\geosolutions\\Desktop\\Shapefile_Tiff\\intact_forest\\intact_forest");
        //File mosaicDir = new File("/home/geosol/data/unredd/ingestion/intact_forest");
        
    	assertTrue(mosaicDir.exists());
        assertTrue(mosaicDir.isDirectory());

        File rasterFile = new File("C:\\Users\\geosolutions\\Desktop\\Shapefile_Tiff\\intact_forest\\intact_forest\\intact_forest_2005.tif");
//        File rasterFile = new File("/home/geosol/data/unredd/ingestion/src/intact_forest_2005.tif");

        double [] bbox = new double[4];
        bbox[0] = -180;
        bbox[1] = 180;
        bbox[2] = -180;
        bbox[3] = 180;
        
        Mosaic mosaic = new Mosaic(geoServerConfig, mosaicDir, getTempDir(), getConfigDir());
        mosaic.add("unredd", "intact_forest", rasterFile, "EPSG:4326", bbox);


//        LOGGER.info("Connecting to " + postGisConfig);
//        assertEquals(48, count(postGisConfig, LAYER));

    }


    private void clearTable(PostGisConfig cfg, String table) throws PostGisException, IOException, SQLException {
        DataStore ds_ = PostGISUtils.createDatastore(cfg);
        assertTrue(ds_ instanceof JDBCDataStore);
        
        JDBCDataStore ds= (JDBCDataStore) ds_;

        Connection connection = ds.getConnection(Transaction.AUTO_COMMIT);
        connection.commit();
        Statement statement = connection.createStatement();
        statement.executeUpdate("DROP TABLE IF EXISTS " + cfg.getSchema() + "." + table );
        connection.commit();
        ds.dispose();
    }

    private long count(PostGisConfig cfg, String table) throws PostGisException, IOException, SQLException {
        DataStore ds_ = PostGISUtils.createDatastore(cfg);
        assertTrue(ds_ instanceof JDBCDataStore);
        
        JDBCDataStore ds= (JDBCDataStore) ds_;

        Connection connection = ds.getConnection(Transaction.AUTO_COMMIT);
        connection.commit();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT count(*) FROM " + cfg.getSchema() + "." + table );
        rs.next();
        long ret = rs.getLong(1);
        ds.dispose();
        return ret;
    }
}
