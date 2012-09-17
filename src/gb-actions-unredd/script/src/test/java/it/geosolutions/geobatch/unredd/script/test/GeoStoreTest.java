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

import static org.junit.Assume.assumeTrue;
import it.geosolutions.geobatch.unredd.script.exception.FlowException;
import it.geosolutions.geobatch.unredd.script.exception.GeoStoreException;
import it.geosolutions.geobatch.unredd.script.test.utils.UNREDDGeoStoreTestUtil;
import it.geosolutions.geostore.core.model.Resource;
import it.geosolutions.unredd.geostore.model.UNREDDCategories;
import it.geosolutions.unredd.geostore.model.UNREDDStatsData;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeoStoreTest extends BaseTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(GeoStoreTest.class);

    /**
     * Needs a running geostore for testing
     */
    @Test
    public void testRunScripts() throws GeoStoreException, IOException, FlowException {
        assertNotNull(getGeoStoreUtil());
        assumeTrue(pingGeoStore());

        // some test data
        UNREDDGeoStoreTestUtil.deleteResources(UNREDDCategories.STATSDATA);

        long id = UNREDDGeoStoreTestUtil.insertStatsData("sdata1", "2010", "1", null, "data01");

        Resource res = gstcu.getFullResource(id);
        UNREDDStatsData sd = new UNREDDStatsData(res);

        assertEquals("sdata1_2010-01", res.getName());
        assertEquals("2010", sd.getAttribute(it.geosolutions.unredd.geostore.model.UNREDDStatsData.Attributes.YEAR));
        assertEquals("1", sd.getAttribute(it.geosolutions.unredd.geostore.model.UNREDDStatsData.Attributes.MONTH));

    }


}
