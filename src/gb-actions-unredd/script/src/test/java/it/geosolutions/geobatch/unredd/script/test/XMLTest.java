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

package it.geosolutions.geobatch.unredd.script.test;

import static org.junit.Assert.assertEquals;
import it.geosolutions.geobatch.unredd.script.model.Request;
import it.geosolutions.geobatch.unredd.script.util.RequestJDOMReader;
import it.geosolutions.unredd.geostore.model.UNREDDFormat;

import java.io.IOException;

import org.jdom.JDOMException;
import org.junit.Test;

public class XMLTest {

    @Test
    public void testCorrectVector() throws JDOMException, IOException {
        Request request = RequestJDOMReader.parseFile("src/test/resources/testXML/RequestVector.xml");
        assertEquals("layer1", request.getLayername());
        assertEquals("2010", request.getYear());
        assertEquals("10", request.getMonth());
        assertEquals(UNREDDFormat.VECTOR, request.getFormat());
    }

    @Test
    public void testCorrectRaster() throws JDOMException, IOException {
        Request request = RequestJDOMReader.parseFile("src/test/resources/testXML/RequestRaster.xml");
        assertEquals("layer1", request.getLayername());
        assertEquals("2012", request.getYear());
        assertEquals(UNREDDFormat.RASTER, request.getFormat());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBadParameterLayerName() throws JDOMException, IOException {
        RequestJDOMReader.parseFile("src/test/resources/testXML/RequestNoLayername.xml");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNoDate() throws JDOMException, IOException {
        RequestJDOMReader.parseFile("src/test/resources/testXML/RequestNoDate.xml");

    }



}
