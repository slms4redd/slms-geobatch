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

import it.geosolutions.geobatch.configuration.event.action.ActionConfiguration;
import it.geosolutions.geobatch.geotiff.overview.GeotiffOverviewsEmbedderConfiguration;
import it.geosolutions.geobatch.geotiff.retile.GeotiffRetilerConfiguration;
import it.geosolutions.geobatch.registry.AliasRegistry;
import it.geosolutions.geobatch.unredd.script.ingestion.IngestionConfiguration;
import it.geosolutions.geobatch.unredd.script.model.GeoServerBasicConfig;
import it.geosolutions.geobatch.unredd.script.model.GeoStoreConfig;
import it.geosolutions.geobatch.unredd.script.model.PostGisConfig;
import it.geosolutions.geobatch.unredd.script.model.RasterizeConfig;
import it.geosolutions.geobatch.unredd.script.reprocess.ReprocessConfiguration;
import it.geosolutions.geobatch.xstream.Alias;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class ConfigurationTest extends BaseTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationTest.class);

    
    public ConfigurationTest() {
    }
    
    @Test
    public void testReprocessConfigFile() throws Exception {
        ReprocessConfiguration cfg = (ReprocessConfiguration)loadActionConfig("configuration/file/reprocessConfigTest.xml");
        assertNotNull(cfg);

        assertEquals("ReprocessGeneratorService", cfg.getServiceID());
        
        assertEquals("cfgId", cfg.getId());
        assertNotNull(cfg.getGeoStoreConfig());
        assertEquals("url0", cfg.getGeoStoreConfig().getUrl());

        assertNotNull(cfg.getPostGisConfig());
        assertEquals("pghost", cfg.getPostGisConfig().getHost());
        assertEquals(42, cfg.getPostGisConfig().getPort());

        assertNotNull(cfg.getRasterizeConfig());

        assertNotNull(cfg.getOverviewsEmbedderConfiguration());
        assertEquals(42, cfg.getOverviewsEmbedderConfiguration().getNumSteps());
    }

    @Test
    public void testIngestionConfigFile() throws Exception {
        IngestionConfiguration cfg = (IngestionConfiguration)loadActionConfig("ingestion/ingestionCfg.xml");
        assertNotNull(cfg);

        assertEquals("unredd", cfg.getGeoServerConfig().getWorkspace());

        assertNotNull(cfg.getRetilerConfiguration());
    }

    private void dumpActionConfig(ActionConfiguration cfg) {

        AliasRegistry aliasRegistry = new AliasRegistry();
        Alias alias = new Alias();
        alias.setAliasRegistry(aliasRegistry);
        // FIXME see improvements done in the latest snapshots of GB 1.4 
        //new ScriptAliasRegistrar(aliasRegistry);
        XStream xstream = new XStream();
        alias.setAliases(xstream);
        
        String xml = xstream.toXML(cfg);
        LOGGER.info("Dumping " + cfg.getClass().getSimpleName() + " ---> \n" + xml);
    }

    @Test
    public void testDump() {
    
        ReprocessConfiguration reprocessCfg = createReprocessConfiguration();
        dumpActionConfig(reprocessCfg);

        IngestionConfiguration ingestionCfg = createIngestionConfiguration();
        dumpActionConfig(ingestionCfg);
    }
    
    
    protected ReprocessConfiguration createReprocessConfiguration() {
        ReprocessConfiguration cfg = new ReprocessConfiguration("cfgId", "cfgName", "cfgDesc");

        GeoStoreConfig gsc = new GeoStoreConfig();
        gsc.setUrl("url0");
        gsc.setUsername("un0");
        gsc.setPassword("pw0");
        cfg.setGeoStoreConfig(gsc);

        PostGisConfig pgc = new PostGisConfig();
        pgc.setHost("pghost");
        pgc.setPort(42);
        pgc.setDatabase("pgdb");
        pgc.setSchema("pgschema");
        pgc.setUsername("pguser");
        pgc.setPassword("pgpw");
        cfg.setPostGisConfig(pgc);

        RasterizeConfig rc = new RasterizeConfig();
        rc.setExecutable("gdal_rasterize");
        rc.setFreeMarkerTemplate("freemarkertemplate.xml");
        rc.setTaskExecutorXslFileName("taskexec.xsl");
        cfg.setRasterizeConfig(rc);

        GeotiffOverviewsEmbedderConfiguration goec = new GeotiffOverviewsEmbedderConfiguration("id","name","descr");
        goec.setNumSteps(42);
        goec.setTileH(512);
        goec.setTileW(512);
        cfg.setOverviewsEmbedderConfiguration(goec);

        return cfg;
    }

    protected IngestionConfiguration createIngestionConfiguration() {
        IngestionConfiguration cfg = new IngestionConfiguration("ingestionId", "IngestionName", "IngestionDesc");

        GeoStoreConfig gsc = new GeoStoreConfig();
        gsc.setUrl("url0");
        gsc.setUsername("un0");
        gsc.setPassword("pw0");
        cfg.setGeoStoreConfig(gsc);

        PostGisConfig pgc = new PostGisConfig();
        pgc.setHost("pghost");
        pgc.setPort(42);
        pgc.setDatabase("pgdb");
        pgc.setSchema("pgschema");
        pgc.setUsername("pguser");
        pgc.setPassword("pgpw");
        cfg.setPostGisConfig(pgc);

        RasterizeConfig rc = new RasterizeConfig();
        rc.setExecutable("gdal_rasterize");
        rc.setFreeMarkerTemplate("freemarkertemplate.xml");
        rc.setTaskExecutorXslFileName("taskexec.xsl");
        cfg.setRasterizeConfig(rc);

        GeotiffRetilerConfiguration grc = new GeotiffRetilerConfiguration("grcId", "grcN", "grcD");
        grc.setTileH(256);
        grc.setTileW(256);
        cfg.setRetilerConfiguration(grc);

        GeotiffOverviewsEmbedderConfiguration goec = new GeotiffOverviewsEmbedderConfiguration("id","name","descr");
        goec.setNumSteps(42);
        goec.setTileH(512);
        goec.setTileW(512);
        cfg.setOverviewsEmbedderConfiguration(goec);

        GeoServerBasicConfig gsac = new GeoServerBasicConfig("gsacId", "gsacName", "gsacDesc");
        gsac.setGeoserverURL("http://it.is/here");
        gsac.setGeoserverUID("it's");
        gsac.setGeoserverPWD("tmpfc");
        gsac.setWorkspace("unredd");
        cfg.setGeoServerConfig(gsac);

        return cfg;
    }
        
}
