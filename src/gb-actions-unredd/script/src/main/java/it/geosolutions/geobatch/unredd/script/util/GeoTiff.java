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

package it.geosolutions.geobatch.unredd.script.util;

import it.geosolutions.geobatch.flow.event.action.ActionException;
import it.geosolutions.geobatch.geotiff.overview.GeotiffOverviewsEmbedderAction;
import it.geosolutions.geobatch.geotiff.overview.GeotiffOverviewsEmbedderConfiguration;
import it.geosolutions.geobatch.geotiff.retile.GeotiffRetilerAction;
import it.geosolutions.geobatch.geotiff.retile.GeotiffRetilerConfiguration;
//import it.geosolutions.geobatch.unredd.script.IngestionActionion;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeoTiff {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(GeoTiff.class);

	public static File retile(File tiffFile, int reTileH, int reTileW, File tempDir) throws ActionException, IOException {
		GeotiffRetilerConfiguration retilerConfig=new GeotiffRetilerConfiguration("id","BRISEIDE_retiler","description");
		retilerConfig.setTileH(reTileH);
		retilerConfig.setTileW(reTileW);

        return retile(retilerConfig, tiffFile, tempDir);
    }

	public static File retile(GeotiffRetilerConfiguration retilerConfig, File tiffFile, File tempDir) throws ActionException, IOException {
	
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Starting retiling "+tiffFile.getAbsolutePath());
		}

		//listenerForwarder.setTask("configuring retiler");

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("Retiling parameters : [reTilerCfg = "+retilerConfig+"]");
		}
//		retilerConfig.setWorkingDirectory(context);

        GeotiffRetilerAction retiler = new GeotiffRetilerAction(retilerConfig);
        retiler.setTempDir(tempDir);

		//listenerForwarder.setTask("starting retiler");
        File outFile = SingleFileActionExecutor.execute(retiler, tiffFile);

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Retiling successfully completed");
		}

        return  outFile;
	}
	
	public static File embedOverviews(GeotiffOverviewsEmbedderConfiguration overviewConfig, File fileTiff, File tempDir) throws ActionException, IOException {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Starting overview");
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Overview parameters : ["+overviewConfig+"]");
		}	
		
		//listenerForwarder.setTask("configuring embedOverviews");
	    //listenerForwarder.setTask("Subsample algorithm defined");
//		overviewConfig.setWorkingDirectory(instanceWorkingDir.getAbsolutePath());
        
		GeotiffOverviewsEmbedderAction overview = new GeotiffOverviewsEmbedderAction(overviewConfig);
        overview.setTempDir(tempDir);
		//listenerForwarder.setTask("starting overviews embedder");

        File outFile = SingleFileActionExecutor.execute(overview, fileTiff);
        if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Overview successfully completed in file " + outFile);
		}	    
		return outFile;
	}

}
