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

package it.geosolutions.geobatch.unredd.script.model;

import it.geosolutions.unredd.geostore.model.UNREDDFormat;
import it.geosolutions.unredd.geostore.utils.NameUtils;

/**
 * 
 * This bean is the model of a layerUpdate request. 
 * Is used by the Unredd flows to deserialize the info.xml file provided as a part of the flow input.
 *
 */
public class Request {

    private String layername;
    private String year;
    private String month;
    private String day;
    
    private UNREDDFormat format;

    public Request() {
    }

    public UNREDDFormat getFormat() {
        return format;
    }

    public void setFormat(UNREDDFormat format) {
        this.format = format;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    /**
     * 
     * @return a String rappresenting the file name of the granule associated to the layerUpdate modelled by this bean.
     */
    public String buildFileName() {
        StringBuilder sb = new StringBuilder();
        sb.append(NameUtils.buildLayerUpdateName(layername, year, month, day));

        if (UNREDDFormat.VECTOR == format ) {
            sb.append(".shp");
        } else if (UNREDDFormat.RASTER == format ) {
            sb.append(".tif");
        } else
            throw new IllegalStateException("Bad format " + format);

        return sb.toString();
    }

    public String getLayername() {
        return layername;
    }

    public void setLayername(String layername) {
        this.layername = layername;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}
    
    

    
}
