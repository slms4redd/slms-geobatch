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

import it.geosolutions.geobatch.unredd.script.model.Request;
import it.geosolutions.unredd.geostore.model.UNREDDFormat;

import java.io.File;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestJDOMReader {

    private final static  Logger LOGGER = LoggerFactory.getLogger(RequestJDOMReader.class);
    
    private final static String ELEM_layerName = "layername";
    private final static String ELEM_year = "year";
    private final static String ELEM_month = "month";
    private final static String ELEM_format = "format";

    public static Request parseFile(String xmlFilename) throws JDOMException, IOException {
        return parseFile(new File(xmlFilename));
    }

    /*************
     * this metod perform the parse of the xml file
     * this should be composed as follows
     *
     * <layer></layer>
     * <format></format> <!-- vector or raster
     * <year></year>
     * <month></month> !-- optionally
     * 
     * @param xmlFile
     * @return
     * @throws JDOMException
     * @throws IOException
     */
    public static Request parseFile(File xmlFile) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(xmlFile);
        Element rootElement = document.getRootElement();

        Request request = new Request();

        //=== Fetch elements
        request.setLayername(rootElement.getChildText(ELEM_layerName));
        request.setYear(rootElement.getChildText(ELEM_year));
        request.setMonth(rootElement.getChildText(ELEM_month));

        String sFormat = rootElement.getChildText(ELEM_format);
        UNREDDFormat format = UNREDDFormat.parseName(sFormat);
        request.setFormat(format);

        //=== Check elems

        if (request.getLayername() == null) {
            throw new IllegalArgumentException("Null <"+ELEM_layerName+">");
        }

        try {
            Integer.parseInt(request.getYear());
        } catch(Exception e) {
            throw new IllegalArgumentException("Bad <"+ELEM_year+"> : " + request.getYear(), e);
        }

        try {
            if(request.getMonth() != null)
                Integer.parseInt(request.getMonth());
        } catch(Exception e) {
            throw new IllegalArgumentException("Bad <"+ELEM_month+"> : " + request.getMonth(), e);
        }

        if (request.getFormat() == null) {
            throw new IllegalArgumentException("Bad <"+ELEM_format+"> : " + sFormat);
        }

        return request;
    }
  
}
