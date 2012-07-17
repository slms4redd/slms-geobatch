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
package it.geosolutions.geobatch.unredd.geostore;

import it.geosolutions.geobatch.catalog.Configuration;
import it.geosolutions.geobatch.configuration.event.action.ActionConfiguration;

/**
 * 
 * @author Luca Paolino - luca.paolino@geo-solutions.it
 * 
 */
public class GeostoreActionConfiguration extends ActionConfiguration implements Configuration {

    public GeostoreActionConfiguration(String id, String name, String description) {
        super(id, name, description);
        // TODO INITIALIZE MEMBERS
    }
    String url = null;
    String user = null;
    String password = null;

    GeostoreOperation.Operation operation = null;
    boolean shortResource = true;

    public GeostoreOperation.Operation getOperation() {
        return operation;
    }

    public void setOperation(GeostoreOperation.Operation operation) {
        this.operation = operation;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isShortResource() {
        return shortResource;
    }

    public void setShortResource(boolean shortResource) {
        this.shortResource = shortResource;
    }

    @Override
    public GeostoreActionConfiguration clone() {
        final GeostoreActionConfiguration ret = new GeostoreActionConfiguration(this.getId(), this.getName(), this.getDescription());

        // TODO CLONE YOUR MEMBERS
        ret.setUrl(url);
        ret.setUser(user);
        ret.setPassword(password);
        ret.setShortResource(shortResource);
        ret.setServiceID(this.getServiceID());
        ret.setListenerConfigurations(ret.getListenerConfigurations());
        return ret;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                +"["
                + "url=" + url
                + ", user=" + user
                + ", super=" + super.toString()
                + ']';
    }
}
