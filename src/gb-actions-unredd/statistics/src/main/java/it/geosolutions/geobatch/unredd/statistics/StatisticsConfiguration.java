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
package it.geosolutions.geobatch.unredd.statistics;

import it.geosolutions.geobatch.catalog.Configuration;
import it.geosolutions.geobatch.configuration.event.action.ActionConfiguration;

/**
 * 
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 * 
 */

public class StatisticsConfiguration extends ActionConfiguration implements Configuration {

	
    public StatisticsConfiguration(String id, String name, String description) {
        super(id, name, description);
	// TODO INITIALIZE MEMBERS
    }
    
	String repositoryDir = null;
	String xmlFileName = null;
	
	
    
    public String getRepositoryDir() {
		return repositoryDir;
	}



	public void setRepositoryDir(String repositoryDir) {
		this.repositoryDir = repositoryDir;
	}



	public String getXmlFileName() {
		return xmlFileName;
	}



	public void setXmlFileName(String xmlFileName) {
		this.xmlFileName = xmlFileName;
	}



	@Override
    public StatisticsConfiguration clone(){
        final StatisticsConfiguration ret=new StatisticsConfiguration(this.getId(), this.getName(), this.getDescription());
        
		// TODO CLONE YOUR MEMBERS
        ret.setRepositoryDir(repositoryDir);
        ret.setXmlFileName(xmlFileName);
        ret.setServiceID(this.getServiceID());
        ret.setListenerConfigurations(ret.getListenerConfigurations());
        return ret;
    }

}
