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
package it.geosolutions.geobatch.unredd.script.test;

import com.thoughtworks.xstream.XStream;
import it.geosolutions.geobatch.configuration.event.action.ActionConfiguration;
import it.geosolutions.geobatch.registry.AliasRegistry;
import it.geosolutions.geobatch.unredd.script.ScriptAliasRegistrar;
import it.geosolutions.geobatch.unredd.script.model.GeoServerBasicConfig;
import it.geosolutions.geobatch.unredd.script.model.GeoStoreConfig;
import it.geosolutions.geobatch.unredd.script.model.PostGisConfig;
import it.geosolutions.geobatch.unredd.script.model.RasterizeConfig;
import it.geosolutions.geobatch.unredd.script.test.utils.GeoStoreTestClientUtil;
import it.geosolutions.geobatch.unredd.script.test.utils.UNREDDGeoStoreTestUtil;
import it.geosolutions.geobatch.unredd.script.util.GeoStoreUtil;
import it.geosolutions.geobatch.xstream.Alias;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.net.URL;

import junit.framework.Assert;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import static org.junit.Assert.*;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public abstract class BaseTest extends Assert{

    private final static Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);

    @Rule
    public TestName _testName = new TestName();
    
    /**
    * @see please use #getTestDir()
    */
    private static File testDataDir = null;
    private File classDir;
    private File tempDir;
    private File configDir;

    /**
     * A geostoreUtils is needed in some FlowUtils calls
     */
    private GeoStoreUtil geostoreUtil;

    protected static GeoStoreTestClientUtil gstcu;

    static protected GeoStoreConfig geoStoreConfig;
    static protected GeoStoreConfig geoStoreConfig2;
    
    static protected PostGisConfig postGisConfig;

    static protected GeoServerBasicConfig geoServerConfig;
    /**
     * Dissemination PG.
     */
    static protected PostGisConfig postGisConfig2; 
    static protected RasterizeConfig rasterizeConfig;

    protected static ClassPathXmlApplicationContext ctx = null;


    protected synchronized GeoStoreUtil getGeoStoreUtil() {
        // we're creating geostoreUtil lazily, bc invoking getTempDir causes it to be created even if we dont need it
        if(geostoreUtil == null)
//            geostoreUtil = new GeoStoreUtil(geostore_url, geostore_username, geostore_password, getTempDir());
            geostoreUtil = new GeoStoreUtil(geoStoreConfig, getTempDir());
        return geostoreUtil;
    }

    protected boolean pingGeoStore() {
        try {
            gstcu.searchLayer("_hope_there_s_no_layer_with_such_name_");
            return true;
        } catch (Exception ex) {
            //... and now for an awful example of heuristic.....
            Throwable t = ex.getCause().getCause().getCause().getCause();
            if(t instanceof ConnectException) {
                LOGGER.warn("Testing GeoStore is offline");                
                return false;
            }
            throw new RuntimeException("Unexpected exception: " + ex.getMessage(), ex);
        }
    }
    
    protected synchronized File getConfigDir() {
        if( ! configDir.exists()) // create dir lazily
        	configDir.mkdir();

        return configDir;
    }

    protected synchronized File getTempDir() {
        if( ! tempDir.exists()) // create dir lazily
            tempDir.mkdir();

        return tempDir;
    }

    @BeforeClass
    public static void loadStaticData() throws Exception {

        if(ctx == null) {
            String[] paths = {
                        "applicationContext-test.xml"
            };
            ctx = new ClassPathXmlApplicationContext(paths);

            geoStoreConfig = (GeoStoreConfig)ctx.getBean("geoStoreConfig");
            geoStoreConfig2 = (GeoStoreConfig)ctx.getBean("geoStoreConfig2");

            geoServerConfig = (GeoServerBasicConfig)ctx.getBean("geoServerConfig");

            postGisConfig = (PostGisConfig)ctx.getBean("postGisConfig");
            postGisConfig2 = (PostGisConfig)ctx.getBean("postGisConfig2");

            rasterizeConfig = (RasterizeConfig)ctx.getBean("rasterizeConfig");

            UNREDDGeoStoreTestUtil.init(geoStoreConfig);
        }

        if(geoStoreConfig == null)
            throw new IllegalStateException("geoStoreConfig is null");

        if(postGisConfig == null)
            throw new IllegalStateException("postGisConfig is null");

        if(rasterizeConfig == null)
            throw new IllegalStateException("rasterizeConfig is null");

        // load geostore params
//        Properties loadedProps = PropertyLoaderTestUtil.load("testdata.properties");
//        Map properties = PropertyLoaderTestUtil.loadIntoConf(loadedProps);
//
//        // create GeoStoreUtil client
//        geostore_url      = (String)properties.get(UNREDDProps.GSTURLS.propName());
//        geostore_username = (String)properties.get(UNREDDProps.GSTUSERS.propName());
//        geostore_password = (String)properties.get(UNREDDProps.GSTPWDS.propName());

        gstcu = new GeoStoreTestClientUtil(geoStoreConfig);
//        gstcu = new GeoStoreTestClientUtil(geostore_url, geostore_username, geostore_password);
        
//        geoStoreConfig = new GeoStoreConfig();
//        geoStoreConfig.setUrl(geostore_url);
//        geoStoreConfig.setUsername(geostore_username);
//        geoStoreConfig.setPassword(geostore_password);
    }

    @Before 
    public synchronized void baseTestBefore() {
        LOGGER.info("---------- Running Test " + getClass().getSimpleName() + " :: " + _testName.getMethodName());

        String className = this.getClass().getSimpleName();
        classDir = new File(getTestDataDir(), className);
        if( ! classDir.exists()) {
            if(LOGGER.isDebugEnabled())
                LOGGER.debug("Using test class dir " + classDir);
            classDir.mkdir();
        }

        String testName = _testName.getMethodName();
        tempDir = new File(classDir, testName);
        configDir = new File(classDir, "GEOBATCH_CONFIG_DIR");
        if(LOGGER.isDebugEnabled())
            LOGGER.debug("Using test case dir " + tempDir);

        // we're creating geostoreUtil lazily, bc invoking getTempDir causes it to be created even if we dont need it
        geostoreUtil = null;
    }

    private synchronized File getTestDataDir() {

        if(testDataDir != null)
            return testDataDir;

        String startDir = System.getProperty("buildDirectory");
        if(startDir == null) {
            LOGGER.warn("Property 'buildDirectory' is not defined");

            File f = loadFile(".");
            if( f== null || ! f.exists() ) {
                LOGGER.warn("Undefined current directory");

                throw new IllegalStateException("Could not find a valid current dir");
            }

            String fa = f.getParentFile().getAbsolutePath();

            if ( ! "target".equals(  FilenameUtils.getBaseName(fa))) {
                LOGGER.warn("Can't use current dir " + fa);
                throw new IllegalStateException("Could not find a valid current dir");
            }

            startDir = fa;
        }

        testDataDir = new File(startDir, "test-data-"+System.currentTimeMillis());

        if(LOGGER.isDebugEnabled())
            LOGGER.debug("Using test dir " + testDataDir);

        testDataDir.mkdir();
        return testDataDir;
    }


    protected File loadFile(String name) {
        try {
            URL url = this.getClass().getClassLoader().getResource(name);
            if (url == null) {
                throw new IllegalArgumentException("Cant get file '" + name + "'");
            }
            File file = new File(url.toURI());
            return file;
        } catch (URISyntaxException e) {
            LOGGER.error("Can't load file " + name + ": " + e.getMessage(), e);
            return null;
        }
    }

    protected ActionConfiguration loadActionConfig(String filename) throws Exception {
        File file = loadFile(filename);
        assertNotNull(file);
        AliasRegistry aliasRegistry = new AliasRegistry();
        new ScriptAliasRegistrar(aliasRegistry); // register static info
        XStream xstream = new XStream();
        Alias alias = new Alias();
        alias.setAliasRegistry(aliasRegistry);
        alias.setAliases(xstream);
        InputStream inStream = null;
        try {
            inStream = new FileInputStream(file);
            return (ActionConfiguration) xstream.fromXML(new BufferedInputStream(inStream));
        } finally {
            if ( inStream != null ) {
                IOUtils.closeQuietly(inStream);
            }
        }
    }

}
