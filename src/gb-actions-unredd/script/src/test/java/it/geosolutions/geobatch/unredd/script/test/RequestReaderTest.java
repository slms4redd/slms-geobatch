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

import it.geosolutions.geobatch.unredd.script.reprocess.model.ReprocessChartRequest;
import it.geosolutions.geobatch.unredd.script.reprocess.model.ReprocessLayerRequest;
import static org.junit.Assert.*;

import java.io.File;
import it.geosolutions.geobatch.unredd.script.reprocess.model.ReprocessRequest;
import it.geosolutions.geobatch.unredd.script.reprocess.model.ReprocessStatsRequest;
import it.geosolutions.geobatch.unredd.script.reprocess.model.RequestReader;
import java.net.URISyntaxException;
import java.net.URL;
import javax.xml.bind.JAXB;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestReaderTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(RequestReaderTest.class);


    @Test
    public void testReprocessLayerMarshalling() {

        // printout a sample request
        ReprocessLayerRequest rsReq = new ReprocessLayerRequest();
        rsReq.setLayerName("testLayerName");
        rsReq.setYear("1492");
        rsReq.setMonth("10");
        JAXB.marshal(rsReq, System.out);

        // test loading / unmarshalling request
        File reproFile = loadFile("reprocess/reprocessLayer.xml");
        assertNotNull("Error finding file", reproFile);

        ReprocessRequest req = RequestReader.load(reproFile);
        assertNotNull("Error unmarshalling file", req);

        assertTrue(req instanceof ReprocessLayerRequest);
        ReprocessLayerRequest sreq = (ReprocessLayerRequest)req;
        assertEquals("1492", sreq.getYear());
    }

    @Test
    public void testReprocessStatsMarshalling() {

        // printout a sample request
        ReprocessStatsRequest rsReq = new ReprocessStatsRequest();
        rsReq.add("testStats1");
        rsReq.add("testStats2");
        JAXB.marshal(rsReq, System.out);

        // test loading / unmarshalling request
        File reproFile = loadFile("reprocess/reprocessStats.xml");
        assertNotNull("Error finding file", reproFile);

        ReprocessRequest req = RequestReader.load(reproFile);
        assertNotNull("Error unmarshalling file", req);

        assertTrue(req instanceof ReprocessStatsRequest);
        ReprocessStatsRequest sreq = (ReprocessStatsRequest)req;
        assertEquals(2, sreq.getStatsNames().size());
    }

    @Test
    public void testReprocessChartMarshalling() {

        // printout a sample request
        ReprocessChartRequest rsReq = new ReprocessChartRequest();
        rsReq.add("testChart1");
        rsReq.add("testChart2");
        JAXB.marshal(rsReq, System.out);

        // test loading / unmarshalling request
        File reproFile = loadFile("reprocess/reprocessChart.xml");
        assertNotNull("Error finding file", reproFile);

        ReprocessRequest req = RequestReader.load(reproFile);
        assertNotNull("Error unmarshalling file", req);

        assertTrue(req instanceof ReprocessChartRequest);
        ReprocessChartRequest sreq = (ReprocessChartRequest)req;
        assertEquals(2, sreq.getChartNames().size());
    }

    protected File loadFile(String name) {
        try {
            URL url = this.getClass().getClassLoader().getResource(name);
            if (url == null) {
                throw new IllegalArgumentException("Cant get file '" + name + "'");
            }
            File file = new File(url.toURI());
            return file;
        } catch (URISyntaxException e) {
            LOGGER.error("Can't load file " + name + ": " + e.getMessage(), e);
            return null;
        }
    }
}
