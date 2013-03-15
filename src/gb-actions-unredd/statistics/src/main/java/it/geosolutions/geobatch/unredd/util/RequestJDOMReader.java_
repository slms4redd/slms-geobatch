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

package it.geosolutions.geobatch.unredd.util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import it.geosolutions.geobatch.unredd.model.Request;

public class RequestJDOMReader {
	
	private final static String AttributelayerName = "layername";
	private final static String AttributeYearName = "year";
	private final static String AttributeTiffName = "tiff";
	
	public static Request get(String xmlFilename) throws JDOMException, IOException{
		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(new File(xmlFilename));
		Element rootElement = document.getRootElement();
		String layername = rootElement.getChild(AttributelayerName).getValue();
		String year = rootElement.getChild(AttributeYearName).getValue();
		String tiff = rootElement.getChild(AttributeTiffName).getValue();
		return new Request(layername, year, tiff);
	}
}
