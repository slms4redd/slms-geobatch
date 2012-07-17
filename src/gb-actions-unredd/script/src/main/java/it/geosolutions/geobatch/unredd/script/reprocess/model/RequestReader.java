/*
 *  Copyright (C) 2007 - 2011 GeoSolutions S.A.S.
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
package it.geosolutions.geobatch.unredd.script.reprocess.model;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RequestReader {
    private final static Logger LOGGER = LoggerFactory.getLogger(RequestReader.class);
    private static final JAXBContext c;

    static {
        JAXBContext tmp = null;
        try {
            tmp = JAXBContext.newInstance(
                    ReprocessChartRequest.class,
                    ReprocessStatsRequest.class,
                    ReprocessLayerRequest.class,
                    ReprocessRequest.class);
        } catch (JAXBException ex) {
            LOGGER.error("Error initt'ing JAXBContext: " + ex.getMessage());
        }
        c = tmp;
    }

    public static ReprocessRequest load(File request) {
        try {
            return (ReprocessRequest)(c.createUnmarshaller().unmarshal(request));
        } catch (JAXBException ex) {
            LOGGER.error("Error loading file " + request+ ": " + ex.getMessage(), ex);
        }
        return null;
    }

}
