/*
 *  GeoBatch - Open Source geospatial batch processing system
 *  https://github.com/nfms4redd/nfms-geobatch
 *  Copyright (C) 2007-2012 GeoSolutions S.A.S.
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
package it.geosolutions.geobatch.unredd.script.ingestion;

import it.geosolutions.geobatch.catalog.Configuration;
import it.geosolutions.geobatch.configuration.event.action.ActionConfiguration;
import it.geosolutions.geobatch.geoserver.GeoServerActionConfig;
import it.geosolutions.geobatch.geotiff.overview.GeotiffOverviewsEmbedderConfiguration;
import it.geosolutions.geobatch.geotiff.retile.GeotiffRetilerConfiguration;
import it.geosolutions.geobatch.unredd.script.model.GeoServerBasicConfig;
import it.geosolutions.geobatch.unredd.script.model.GeoStoreConfig;
import it.geosolutions.geobatch.unredd.script.model.PostGisConfig;
import it.geosolutions.geobatch.unredd.script.model.RasterizeConfig;
import java.io.File;

/**
 * 
 */

public class IngestionConfiguration extends ActionConfiguration implements Configuration {

    private GeoStoreConfig geoStoreConfig;
    private PostGisConfig  postGisConfig;
    private RasterizeConfig  rasterizeConfig;
    private GeotiffOverviewsEmbedderConfiguration overviewsEmbedderConfiguration;
    private GeotiffRetilerConfiguration retilerConfiguration;
    private GeoServerBasicConfig geoServerConfig;
    private File originalDataTargetDir;
    /**
     * The absolute path of datastore.properties (STAGING) used in the ImageMosaic action.
     */
    private String datastorePath;
	
    public IngestionConfiguration(String id, String name, String description) {
        super(id, name, description);
    }

    protected IngestionConfiguration() {
        super(null, "Default IngestionConfiguration", "Unset configuration");
    }

    public GeoStoreConfig getGeoStoreConfig() {
        return geoStoreConfig;
    }

    public void setGeoStoreConfig(GeoStoreConfig geoStoreConfig) {
        this.geoStoreConfig = geoStoreConfig;
    }

    public GeotiffOverviewsEmbedderConfiguration getOverviewsEmbedderConfiguration() {
        return overviewsEmbedderConfiguration;
    }

    public void setOverviewsEmbedderConfiguration(GeotiffOverviewsEmbedderConfiguration overviewsEmbedderConfiguration) {
        this.overviewsEmbedderConfiguration = overviewsEmbedderConfiguration;
    }

    public PostGisConfig getPostGisConfig() {
        return postGisConfig;
    }

    public void setPostGisConfig(PostGisConfig postGisConfig) {
        this.postGisConfig = postGisConfig;
    }

    public RasterizeConfig getRasterizeConfig() {
        return rasterizeConfig;
    }

    public void setRasterizeConfig(RasterizeConfig rasterizeConfig) {
        this.rasterizeConfig = rasterizeConfig;
    }

    public GeoServerBasicConfig getGeoServerConfig() {
        return geoServerConfig;
    }

    public void setGeoServerConfig(GeoServerBasicConfig geoServerConfig) {
        this.geoServerConfig = geoServerConfig;
    }

    public File getOriginalDataTargetDir() {
        return originalDataTargetDir;
    }

    public void setOriginalDataTargetDir(File originalDataTargetDir) {
        this.originalDataTargetDir = originalDataTargetDir;
    }

    public GeotiffRetilerConfiguration getRetilerConfiguration() {
        return retilerConfiguration;
    }

    public void setRetilerConfiguration(GeotiffRetilerConfiguration retilerConfiguration) {
        this.retilerConfiguration = retilerConfiguration;
    }
    
    public String getDatastorePath() {
        return datastorePath;
    }
    
    public void setDatastorePath(String datastorePath) {
        this.datastorePath = datastorePath;
    }

    @Override
    public IngestionConfiguration clone(){
        final IngestionConfiguration ret = (IngestionConfiguration)super.clone();

        if(this.geoStoreConfig != null)
            ret.setGeoStoreConfig(this.geoStoreConfig.clone());

        if(this.postGisConfig != null)
            ret.setPostGisConfig(this.postGisConfig.clone());

        if(this.rasterizeConfig != null)
            ret.setRasterizeConfig(this.rasterizeConfig.clone());

        if(this.overviewsEmbedderConfiguration != null)
            ret.setOverviewsEmbedderConfiguration(overviewsEmbedderConfiguration.clone());

        if(this.retilerConfiguration != null)
            ret.setRetilerConfiguration(retilerConfiguration.clone());

        if(this.geoServerConfig != null)
            ret.setGeoServerConfig(geoServerConfig.clone());

        return ret;
    }

}
