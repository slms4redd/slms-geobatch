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
import it.geosolutions.geobatch.unredd.geostore.GeostoreAction;
import it.geosolutions.geobatch.unredd.geostore.GeostoreActionConfiguration;
import it.geosolutions.geobatch.unredd.geostore.GeostoreOperation;
import it.geosolutions.geobatch.unredd.geostore.utils.JAXBMarshallerBuilder;
import it.geosolutions.geobatch.unredd.script.exception.GeoStoreException;
import it.geosolutions.geobatch.unredd.script.model.GeoStoreConfig;
import it.geosolutions.geostore.services.dto.search.SearchFilter;
import it.geosolutions.geostore.services.rest.model.RESTResource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * A gestore Facade using the GeoStoreAction
 *
 * A GeoStoreUtil action is used to execute the various operations.
 */
public class GeoStoreUtil extends GeoStoreFacade {

    private final Logger LOGGER = LoggerFactory.getLogger(GeoStoreUtil.class);

    /** Temp dir used in this very class.
     * This class needs some temp files: they will be stored in the same runningWorkingDir the GeoStoreUtil action will use. */
    private File   tempDir = null;


    public GeoStoreUtil(String url, String user, String pwd, File tempDir) {
        super(url, user, pwd);

        if(LOGGER.isDebugEnabled())
            LOGGER.debug("Temp dir is: " + tempDir);
        this.tempDir = tempDir;
    }

    public GeoStoreUtil(GeoStoreConfig config, File tempDir) {
        this(config.getUrl(), config.getUsername(), config.getPassword(), tempDir);
    }

    protected GeostoreActionConfiguration createConfiguration() {
        GeostoreActionConfiguration searchConfiguration = new GeostoreActionConfiguration("id", "name", " description");
        searchConfiguration.setUrl(getConfigUrl());
        searchConfiguration.setUser(getConfigUsername());
        searchConfiguration.setPassword(getConfigPassword());
//        searchConfiguration.setWorkingDirectory(runningWorkingDir); // really needed?
        return searchConfiguration;
    }


    @Override
    protected List search(SearchFilter filter, boolean getShortResource) throws GeoStoreException {
        return search(filter, getShortResource, "filter");
    }

    /**
     * Generic search in GeoStoreUtil.
     *
     * @param filter the filter to apply for searching
     * @param getShortResource true if a list of resource is required, false if a RESTResource list is sufficient
     *
     * @return always a not null list
     */
//    @Override
    protected List search(SearchFilter filter, boolean getShortResource, String fileNameHint) throws GeoStoreException {
        File tmpFile = null;

        try {
            tmpFile = File.createTempFile(fileNameHint, ".xml", tempDir);
            
            Marshaller m = JAXBMarshallerBuilder.getJAXBMarshaller(filter.getClass());
            m.marshal(filter, tmpFile);

            GeostoreActionConfiguration geoStoreCfg = createConfiguration();
            geoStoreCfg.setShortResource(getShortResource);
            geoStoreCfg.setOperation(GeostoreOperation.Operation.SEARCH);

            GeostoreAction action = new GeostoreAction(geoStoreCfg);
            action.setTempDir(tempDir);

            SingleFileActionExecutor.execute(action, tmpFile);

            List ret = getShortResource
                    ? action.getShortResourceList()
                    : action.getResourceList();
            return ret == null ? Collections.EMPTY_LIST : ret;

        } catch(Exception e) {
            throw new GeoStoreException("Error while searching in GeoStore", e);
        } finally {
            FileUtils.deleteQuietly(tmpFile); // we're putting the temp file in a working dir, so this delete is probably useless
        }
    }
    
    /**
     * generic insert into geostore
     *
     * @param resource the resource to insert
     * @throws GeoStoreException 
     */
    @Override
    public Long insert(RESTResource resource) throws GeoStoreException {
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating " + resource.getCategory().getName() + " --> " + resource.getName());
        }

        String hintName = resource.getCategory().getName() + "_" + resource.getName();
        // this part creates the file to feed the GeoStoreAction
        File tmpFile = null;
        try {
            tmpFile = File.createTempFile("Insert_"+hintName , ".xml", tempDir);
            Marshaller m = JAXBMarshallerBuilder.getJAXBMarshaller(resource.getClass());
            m.marshal(resource, tmpFile);

            GeostoreActionConfiguration geoStoreCfg = createConfiguration();
            geoStoreCfg.setShortResource(true);
            geoStoreCfg.setOperation(GeostoreOperation.Operation.INSERT);

            GeostoreAction action = new GeostoreAction(geoStoreCfg);
            action.setTempDir(tempDir);

            SingleFileActionExecutor.execute(action, tmpFile);
            return null; // no id here
        } catch(Exception e) {
            LOGGER.error("Error while inserting in GeoStore: " + resource);
            throw new GeoStoreException("Error while inserting in GeoStore", e);
        } finally {
            FileUtils.deleteQuietly(tmpFile); // we're putting the temp file in a working dir, so this delete is probably useless
        }
    }

    @Override
    public void updateData(long id, String data) throws GeoStoreException {
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Updating data for resource " + id);
        }
        
        // this part creates the file to feed the GeoStoreAction
        File tmpFile = null;
        try {
            RESTResource resource = new RESTResource(); // this is just a packaging for id+data
            resource.setId(id);
            resource.setData(data);            
            tmpFile = File.createTempFile("UpdateData", ".xml", tempDir);
            Marshaller m = JAXBMarshallerBuilder.getJAXBMarshaller(resource.getClass());
            m.marshal(resource, tmpFile);

            GeostoreActionConfiguration geoStoreCfg = createConfiguration();
            geoStoreCfg.setShortResource(true);
            geoStoreCfg.setOperation(GeostoreOperation.Operation.UPDATEDATA);

            GeostoreAction action = new GeostoreAction(geoStoreCfg);
            action.setTempDir(tempDir);

            SingleFileActionExecutor.execute(action, tmpFile);
        } catch (Exception ex) {
            throw new GeoStoreException("Exception while updating data", ex);
        } finally {
            FileUtils.deleteQuietly(tmpFile); // we're putting the temp file in a working dir, so this delete is probably useless
        }
    }


    /**
     * Delete a resource.
     * Delete the resource identified by id.
     *
     * @param id
     */
    @Override
    public void delete(long id) throws GeoStoreException {

        try {
            RESTResource resource = new RESTResource();
            resource.setId(id);

            // this part creates the file to feed the GeoStoreAction
            File inputFile = File.createTempFile("Delete", ".xml", tempDir);
            Marshaller m = JAXBMarshallerBuilder.getJAXBMarshaller(resource.getClass());
            m.marshal(resource, inputFile);

            GeostoreActionConfiguration geoStoreCfg = createConfiguration();
            geoStoreCfg.setShortResource(true);
            geoStoreCfg.setOperation(GeostoreOperation.Operation.DELETE);

            GeostoreAction action = new GeostoreAction(geoStoreCfg);
            action.setTempDir(tempDir);

            SingleFileActionExecutor.execute(action, inputFile);
        } catch (Exception ex) {
            throw new GeoStoreException("Error while deleting resource " + id, ex);
        }
    }
    
    private void doXStreamMarshall(SearchFilter resource, File tmpFile)  throws GeoStoreException {
    	
    	
        
        XStream xStream = new XStream(new DomDriver());
        xStream.alias("AND", it.geosolutions.geostore.services.dto.search.AndFilter.class);
        xStream.alias("FIELD",it.geosolutions.geostore.services.dto.search.FieldFilter.class);
        xStream.alias("CATEGORY",it.geosolutions.geostore.services.dto.search.CategoryFilter.class);
        BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(tmpFile));
			out.write(xStream.toXML(resource));
		} catch (IOException e) {
            LOGGER.error("Error while inserting in GeoStore: " + resource);
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				LOGGER.error("Error while inserting in GeoStore: " + resource);
			}
		}
        
    	
    	
//    	try{
//	    	JAXBContext jaxbContext = JAXBContext.newInstance(resource.getClass().getCanonicalName());
//			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//	 
//			// output pretty printed
//			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//	 
//			jaxbMarshaller.marshal(resource, tmpFile);
//			jaxbMarshaller.marshal(resource, System.out);
//    	}
//    	catch (JAXBException e){
//    		throw new GeoStoreException("Error while marshalling a RESTResource " + e);
//    	}
    	
    }

}
