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
package it.geosolutions.geobatch.unredd.script.test.utils;

import it.geosolutions.geobatch.unredd.script.test.GDALRasterizeTest;
import it.geosolutions.geobatch.unredd.script.util.FlowUtil;
import it.geosolutions.geostore.core.model.Resource;
import it.geosolutions.geostore.services.dto.ShortResource;
import it.geosolutions.geostore.services.dto.search.BaseField;
import it.geosolutions.geostore.services.dto.search.FieldFilter;
import it.geosolutions.geostore.services.dto.search.SearchOperator;
import it.geosolutions.geostore.services.rest.GeoStoreClient;
import it.geosolutions.geostore.services.rest.model.RESTResource;
import it.geosolutions.geostore.services.rest.model.ShortResourceList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.bind.JAXB;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class ResourceLoader {

    private final static Logger LOGGER = LoggerFactory.getLogger(GDALRasterizeTest.class);

//    @Test
    public void loadResources(GeoStoreClient geostore) throws FileNotFoundException, IOException {
        deleteAllResources(geostore);


        File geostoreDir = loadFile("georepo/resources");
        for (File resFile : geostoreDir.listFiles((FilenameFilter)new PrefixFileFilter("resource"))) {
            Resource res = JAXB.unmarshal(resFile, Resource.class);
            LOGGER.info("LOADED " + res.getCategory().getName() + " : " + res.getName());
            
            String basename = FilenameUtils.getBaseName(resFile.getName());
            String sid = basename.substring(basename.indexOf("_")+1);
            String dataFileName = "data_"+sid+".txt";
            File dataFile = new File(geostoreDir,dataFileName);
            String data = IOUtils.toString(new FileReader(dataFile));

            RESTResource restRes = FlowUtil.copyResource(res);
            restRes.setData(data);

            LOGGER.info("INSERTING " + res.getCategory().getName() + " : " + res.getName());
            geostore.insert(restRes);
        }

    }

    public void deleteAllResources(GeoStoreClient geostore) {
        ShortResourceList sres = geostore.searchResources(new FieldFilter(BaseField.NAME, "*", SearchOperator.IS_NOT_NULL));
        if( ! sres.isEmpty() ) {
            LOGGER.info("DeleteAll: deleting " + sres.getList().size() + " resources");
            for (ShortResource shortResource : sres.getList()) {
                LOGGER.info("DeleteAll: deleting resource #"+shortResource.getId()+ " " + shortResource.getName());
                geostore.deleteResource(shortResource.getId());
            }
        }
    }


    protected File loadFile(String name) {
        try {
            URL url = this.getClass().getClassLoader().getResource(name);
            if(url == null)
                throw new IllegalArgumentException("Cant get file '"+name+"'");
            File file = new File(url.toURI());
            return file;
        } catch (URISyntaxException e) {
            LOGGER.error("Can't load file " + name + ": " + e.getMessage(), e);
            return null;
        }
    }
}
