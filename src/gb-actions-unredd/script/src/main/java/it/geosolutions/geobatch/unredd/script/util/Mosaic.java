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
import it.geosolutions.geobatch.geoserver.GeoServerActionConfig;
import it.geosolutions.geobatch.imagemosaic.ImageMosaicAction;
import it.geosolutions.geobatch.imagemosaic.ImageMosaicCommand;
import it.geosolutions.geobatch.imagemosaic.ImageMosaicConfiguration;
import it.geosolutions.geobatch.imagemosaic.config.DomainAttribute;
import it.geosolutions.unredd.geostore.utils.NameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a Facade to the ImageMosaicAction. 
 * Its methods creates the the ImageMosaicConfigurations from the supplyed parameters, the flow configuration or eventually from other exterbnal resources.
 * Then it is responsible for run executing imagemosaicAction. 
 * @author DamianoG
 *
 */
public class Mosaic {
    private final static Logger LOGGER = LoggerFactory.getLogger(Mosaic.class);

    final private GeoServerActionConfig geoServerCfg;
    final private File tempDir;
    final private File mosaicDir;
    final private File configDir;

    /**********
     * 
     */
    public Mosaic(GeoServerActionConfig geoServerActionConfig, File mosaicDir, File tempDir,  File configDir) {

        this.geoServerCfg = geoServerActionConfig;
        this.tempDir = tempDir;
        this.mosaicDir = mosaicDir;
        this.configDir = configDir;
    }

  /**
   * Execute the imagemosaic action. 
   * Before execute the action setting this method set its configuration and provide a command that add
   * a new granule. 
   * This method works both in case of the granule is the first granule (so all the initialization steps must be done)
   * or the N granule added.
   * 
   * TODO: refactor it due to much parameters are needed. (DamianoG 11-02-2013)
   *   
   * 
   * @param workspace
   * @param mosaicName
   * @param tiff the granule file
   * @param crs CoordinateReferenceSystem
   * @param bbox order must be minX maxX minY maxY
   * @param datastorePath the absolute path of the datastore.properties file
   * 
   * @throws SecurityException
   * @throws ActionException
   * @throws IOException
   */
    public void add(String workspace, String mosaicName, File tiff, String crs, double bbox[], String style, String datastorePath) throws SecurityException, ActionException, IOException {
        
        if ( ! tiff.exists())
            throw new FileNotFoundException("TIF file not found: "+tiff);

        ImageMosaicCommand imageMosaicCommand = new ImageMosaicCommand(mosaicDir, Arrays.asList(tiff), Collections.EMPTY_LIST);

        File commandFile = File.createTempFile("mosaicCommand_", ".xml", tempDir);
        ImageMosaicCommand.serialize(imageMosaicCommand, commandFile.getAbsolutePath());

        ImageMosaicConfiguration imageMosaicConfiguration = new ImageMosaicConfiguration("ImageMosaic", "Configuring mosaic", null);

        if(geoServerCfg == null) {
            if(LOGGER.isWarnEnabled()){LOGGER.warn("GeoServer configuration is missing. is this a test?");}
            imageMosaicConfiguration.setIgnoreGeoServer(true);

            imageMosaicConfiguration.setGeoserverURL("http://localhost:9/geoserver");
            imageMosaicConfiguration.setGeoserverUID("dummy");
            imageMosaicConfiguration.setGeoserverPWD("dummy");
        } else {
            imageMosaicConfiguration.setGeoserverURL(geoServerCfg.getGeoserverURL());
            imageMosaicConfiguration.setGeoserverUID(geoServerCfg.getGeoserverUID());
            imageMosaicConfiguration.setGeoserverPWD(geoServerCfg.getGeoserverPWD());
        }

        imageMosaicConfiguration.setDefaultNamespace(workspace);
        imageMosaicConfiguration.setCrs(crs);
        imageMosaicConfiguration.setConfigDir(mosaicDir);
        imageMosaicConfiguration.setDefaultStyle(style);
        imageMosaicConfiguration.setAllowMultithreading(true);
        imageMosaicConfiguration.setLatLonMinBoundingBoxX(bbox[0]);
        imageMosaicConfiguration.setLatLonMinBoundingBoxY(bbox[1]);
        imageMosaicConfiguration.setLatLonMaxBoundingBoxX(bbox[2]);
        imageMosaicConfiguration.setLatLonMaxBoundingBoxY(bbox[3]);
        imageMosaicConfiguration.setDatastorePropertiesPath(datastorePath);
        
        // Setting up the new domain attributes
        DomainAttribute timeAttribute = new DomainAttribute();
        timeAttribute.setAttribName(DomainAttribute.DIM_TIME);
        timeAttribute.setRegEx(NameUtils.TIME_REGEX);
        timeAttribute.setPresentationMode("LIST");
        timeAttribute.setDimensionName(DomainAttribute.DIM_TIME);
        imageMosaicConfiguration.setDomainAttributes(Arrays.asList(timeAttribute));
        
        if(LOGGER.isInfoEnabled()){LOGGER.info("Time_Regex used is: " + NameUtils.TIME_REGEX);}
        
        ImageMosaicAction imageMosaicAction = new ImageMosaicAction(imageMosaicConfiguration);
        imageMosaicAction.setTempDir(tempDir);
        imageMosaicAction.setConfigDir(configDir);
        
        SingleFileActionExecutor.execute(imageMosaicAction, commandFile);
    }
}
