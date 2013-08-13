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


import it.geosolutions.filesystemmonitor.monitor.FileSystemEvent;
import it.geosolutions.geobatch.catalog.impl.BaseService;
import it.geosolutions.geobatch.flow.event.action.ActionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class PublishingGeneratorService extends BaseService
    implements ActionService<FileSystemEvent, PublishingConfiguration> {
    
    
    public PublishingGeneratorService(String id, String name, String description) {
        super(id, name, description);
    }

    private final static Logger LOGGER = LoggerFactory.getLogger(PublishingGeneratorService.class);

    public PublishingAction createAction(PublishingConfiguration configuration) {
        try {
            return new PublishingAction(configuration);
        } catch (Exception e) {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn(e.getLocalizedMessage(), e);
            return null;
        }
    }

    public boolean canCreateAction(PublishingConfiguration configuration) {
            if (configuration.getSrcGeoServerConfig() == null) {
                LOGGER.error("Source GeoServer config is null");
                return false;
            }
            if (configuration.getSrcGeoStoreConfig() == null) {
                LOGGER.error("Source GeoStore config is null");
                return false;
            }
            if (configuration.getSrcPostGisConfig() == null) {
                LOGGER.error("Source PostGis config is null");
                return false;
            }

            if (configuration.getDstGeoServerConfig() == null) {
                LOGGER.error("Target GeoServer config is null");
                return false;
            }
            if (configuration.getDstGeoStoreConfig() == null) {
                LOGGER.error("Target GeoStore config is null");
                return false;
            }
            if (configuration.getDstPostGisConfig() == null) {
                LOGGER.error("Target PostGis config is null");
                return false;
            }

        return true;
    }

}
