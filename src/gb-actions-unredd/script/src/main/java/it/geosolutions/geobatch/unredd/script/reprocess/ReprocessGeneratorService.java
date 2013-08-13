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
package it.geosolutions.geobatch.unredd.script.reprocess;


import it.geosolutions.filesystemmonitor.monitor.FileSystemEvent;
import it.geosolutions.geobatch.catalog.impl.BaseService;
import it.geosolutions.geobatch.flow.event.action.ActionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class ReprocessGeneratorService extends BaseService
    implements ActionService<FileSystemEvent, ReprocessConfiguration> {
    
    
    public ReprocessGeneratorService(String id, String name, String description) {
        super(id, name, description);
    }

    private final static Logger LOGGER = LoggerFactory.getLogger(ReprocessGeneratorService.class);

    public ReprocessAction createAction(ReprocessConfiguration configuration) {
        try {
            return new ReprocessAction(configuration);
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            return null;
        }
    }

    public boolean canCreateAction(ReprocessConfiguration configuration) {
            if (configuration.getGeoStoreConfig() == null) {
                LOGGER.error("GeoStore config is null");
                return false;
            }
            if (configuration.getOverviewsEmbedderConfiguration() == null) {
                LOGGER.error("OverviewsEmbedder config is null");
                return false;
            }
            if (configuration.getPostGisConfig() == null) {
                LOGGER.error("PostGis config is null");
                return false;
            }
            if (configuration.getRasterizeConfig() == null) {
                LOGGER.error("Rasterize config is null");
                return false;
            }

        return true;
    }

}
