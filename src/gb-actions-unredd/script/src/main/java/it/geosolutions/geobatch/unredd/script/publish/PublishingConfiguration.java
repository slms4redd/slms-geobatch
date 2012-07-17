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
package it.geosolutions.geobatch.unredd.script.publish;

import java.util.Map;

import it.geosolutions.geobatch.catalog.Configuration;
import it.geosolutions.geobatch.configuration.event.action.ActionConfiguration;
import it.geosolutions.geobatch.unredd.script.model.GeoServerBasicConfig;
import it.geosolutions.geobatch.unredd.script.model.GeoStoreConfig;
import it.geosolutions.geobatch.unredd.script.model.PostGisConfig;

/**
 * 
 */
public class PublishingConfiguration extends ActionConfiguration implements Configuration {

    private GeoStoreConfig srcGeoStoreConfig;
    private PostGisConfig  srcPostGisConfig;
    private GeoServerBasicConfig srcGeoServerConfig;

    private GeoStoreConfig dstGeoStoreConfig;
    private PostGisConfig  dstPostGisConfig;
    private GeoServerBasicConfig dstGeoServerConfig;
	
    public PublishingConfiguration(String id, String name, String description) {
        super(id, name, description);
    }

    public GeoServerBasicConfig getDstGeoServerConfig() {
        return dstGeoServerConfig;
    }

    public void setDstGeoServerConfig(GeoServerBasicConfig dstGeoServerConfig) {
        this.dstGeoServerConfig = dstGeoServerConfig;
    }

    public GeoStoreConfig getDstGeoStoreConfig() {
        return dstGeoStoreConfig;
    }

    public void setDstGeoStoreConfig(GeoStoreConfig dstGeoStoreConfig) {
        this.dstGeoStoreConfig = dstGeoStoreConfig;
    }

    public PostGisConfig getDstPostGisConfig() {
        return dstPostGisConfig;
    }

    public void setDstPostGisConfig(PostGisConfig dstPostGisConfig) {
        this.dstPostGisConfig = dstPostGisConfig;
    }

    public GeoServerBasicConfig getSrcGeoServerConfig() {
        return srcGeoServerConfig;
    }

    public void setSrcGeoServerConfig(GeoServerBasicConfig srcGeoServerConfig) {
        this.srcGeoServerConfig = srcGeoServerConfig;
    }

    public GeoStoreConfig getSrcGeoStoreConfig() {
        return srcGeoStoreConfig;
    }

    public void setSrcGeoStoreConfig(GeoStoreConfig srcGeoStoreConfig) {
        this.srcGeoStoreConfig = srcGeoStoreConfig;
    }

    public PostGisConfig getSrcPostGisConfig() {
        return srcPostGisConfig;
    }

    public void setSrcPostGisConfig(PostGisConfig srcPostGisConfig) {
        this.srcPostGisConfig = srcPostGisConfig;
    }


	@Override
    public PublishingConfiguration clone(){
        final PublishingConfiguration ret = (PublishingConfiguration)super.clone();

        if(this.srcGeoStoreConfig != null)
            ret.srcGeoStoreConfig = this.srcGeoStoreConfig.clone();

        if(this.srcGeoServerConfig != null)
            ret.srcGeoServerConfig = this.srcGeoServerConfig.clone();

        if(this.srcPostGisConfig != null)
            ret.srcPostGisConfig = this.srcPostGisConfig.clone();

        if(this.dstGeoStoreConfig != null)
            ret.dstGeoStoreConfig = this.dstGeoStoreConfig.clone();

        if(this.dstGeoServerConfig != null)
            ret.dstGeoServerConfig = this.dstGeoServerConfig.clone();

        if(this.dstPostGisConfig != null)
            ret.dstPostGisConfig = this.dstPostGisConfig.clone();

        return ret;
    }

}
