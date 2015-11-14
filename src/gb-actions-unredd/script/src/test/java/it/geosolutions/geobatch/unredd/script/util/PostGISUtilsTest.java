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
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class PostGISUtilsTest extends BaseTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(PostGISUtilsTest.class);


    public PostGISUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Ignore
    @Test
    public void testShapeToPg() throws Exception {

        final String LAYER = "test0";
        clearTable(postGisConfig, LAYER);

        File shape = loadFile("shp/cell/cell_pol_roi2.shp");
        assertNotNull(shape);
        assertTrue(shape.exists());

        LOGGER.info("Connecting to " + postGisConfig);

        PostGISUtils.shapeToPostGis(shape, postGisConfig, LAYER, "2012", "01", "01");
        assertEquals(48, count(postGisConfig, LAYER));

        PostGISUtils.shapeToPostGis(shape, postGisConfig, LAYER, "2012", "02", "28");
        assertEquals(96, count(postGisConfig, LAYER));

    }

    @Ignore
    @Test
    public void testBadMismatchShapeToPg() throws Exception {

        final String LAYER = "test0";
        clearTable(postGisConfig, LAYER);

        File shape1 = loadFile("shp/cell/cell_pol_roi2.shp");
        assertNotNull(shape1);
        assertTrue(shape1.exists());
//        File shape2 = loadFile("shp/layer1/layer1_2012.shp");
        File shape2 = loadFile("context/data/layer1_2010-10.shp");
        assertNotNull(shape2);
        assertTrue(shape2.exists());

        LOGGER.info("Connecting to " + postGisConfig);

        PostGISUtils.shapeToPostGis(shape2, postGisConfig, LAYER, "2012", "01", "01");
        assertEquals(757, count(postGisConfig, LAYER));

        try {
            PostGISUtils.shapeToPostGis(shape1, postGisConfig, LAYER, "2012", "02", "28");
            fail("Exception not trapped");
        } catch (PostGisException e) {
            LOGGER.info("Exception properly trapped: " + e.getMessage());
        } 
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
