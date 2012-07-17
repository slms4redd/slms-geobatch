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

package it.geosolutions.geobatch.unredd.script.test.utils;

import it.geosolutions.geobatch.unredd.script.util.*;
import it.geosolutions.geostore.core.model.Resource;
import it.geosolutions.geostore.services.dto.ShortResource;
import it.geosolutions.geostore.services.dto.search.SearchFilter;
import it.geosolutions.geostore.services.rest.model.RESTResource;
import it.geosolutions.geobatch.unredd.script.exception.GeoStoreException;
import it.geosolutions.geobatch.unredd.script.model.GeoStoreConfig;
import it.geosolutions.geostore.core.model.StoredData;
import it.geosolutions.geostore.services.rest.GeoStoreClient;
import it.geosolutions.geostore.services.rest.model.ShortResourceList;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A gestore Facade using the GeoStoreAction
 *
 * A GeoStoreUtil action is used to execute the various operations.
 */
public class GeoStoreTestClientUtil extends GeoStoreFacade {

    private final Logger LOGGER = LoggerFactory.getLogger(GeoStoreTestClientUtil.class);
    private final GeoStoreClient geoStoreClient;


    public GeoStoreTestClientUtil(String url, String user, String pwd) {
        super(url, user, pwd);

        geoStoreClient = new GeoStoreClient();
        geoStoreClient.setGeostoreRestUrl(url);
        geoStoreClient.setUsername(user);
        geoStoreClient.setPassword(pwd);
    }

    public GeoStoreTestClientUtil(GeoStoreConfig config) {
        this(config.getUrl(), config.getUsername(), config.getPassword());
    }

    public GeoStoreClient getGeoStoreClient() {
        return geoStoreClient;
    }

    public Resource getFullResource(long id) {
        Resource rest = geoStoreClient.getResource(id);
        // retrieve storedData
        String data = geoStoreClient.getData(id);
        StoredData sd = new StoredData();
        sd.setData(data);
        // reattach storedData
        rest.setData(sd);

        return rest;
    }

    /**
     * Generic search in GeoStoreUtil.
     *
     * @param filter the filter to apply for searching
     * @param getShortResource true if a list of resource is required, false if a RESTResource list is sufficient
     *
     * @return always a not null list
     */
    @Override
    protected List search(SearchFilter filter, boolean getShortResource) throws GeoStoreException {
        try{
            ShortResourceList srl = geoStoreClient.searchResources(filter);
            if(srl == null || srl.getList() == null)
                return Collections.EMPTY_LIST;

            if(getShortResource) {
                return srl.getList();
            } else {
                List<Resource> ret = new ArrayList<Resource>(srl.getList().size());
                for (ShortResource shortResource : srl.getList()) {
                    Resource r = geoStoreClient.getResource(shortResource.getId());
                    String data = geoStoreClient.getData(shortResource.getId());
                    StoredData sdata = new StoredData();
                    sdata.setData(data);
                    r.setData(sdata);
                    ret.add(r);
                }
                return ret;
            }
        } catch(Exception e) {
            throw new GeoStoreException("Error while searching in GeoStore", e);
        }
    }

    @Override
    protected List search(SearchFilter filter, boolean getShortResource, String fileNameHint) throws GeoStoreException {
        return search(filter, getShortResource); // fileNameHins is useless in this implementation
    }

    /**
     * generic insert into geostore
     *
     * @param resource the resource to insert
     * @throws GeoStoreException 
     */
    @Override
    public Long insert(RESTResource resource) throws GeoStoreException {
        try {
            return geoStoreClient.insert(resource);
        } catch(Exception e) {
            throw new GeoStoreException("Error while inserting in GeoStore", e);
        }
    }

    @Override
    public void updateData(long id, String data) throws GeoStoreException {
        try {
            geoStoreClient.setData(id, data);
        } catch (Exception ex) {
            throw new GeoStoreException("Exception while updating data", ex);
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
            geoStoreClient.deleteResource(id);
        } catch (Exception ex) {
            throw new GeoStoreException("Error while deleting resource " + id, ex);
        }
    }

}
