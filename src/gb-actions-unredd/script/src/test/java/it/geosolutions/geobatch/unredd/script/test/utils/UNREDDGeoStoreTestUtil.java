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

package it.geosolutions.geobatch.unredd.script.test.utils;


import it.geosolutions.geobatch.unredd.script.model.GeoStoreConfig;
import it.geosolutions.geostore.services.dto.ShortResource;
import it.geosolutions.geostore.services.dto.search.BaseField;
import it.geosolutions.geostore.services.dto.search.CategoryFilter;
import it.geosolutions.geostore.services.dto.search.FieldFilter;
import it.geosolutions.geostore.services.dto.search.SearchFilter;
import it.geosolutions.geostore.services.dto.search.SearchOperator;
import it.geosolutions.geostore.services.rest.GeoStoreClient;
import it.geosolutions.geostore.services.rest.model.RESTResource;
import it.geosolutions.geostore.services.rest.model.ShortResourceList;
import it.geosolutions.unredd.geostore.model.UNREDDCategories;
import it.geosolutions.unredd.geostore.model.UNREDDLayer;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UNREDDGeoStoreTestUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(UNREDDGeoStoreTestUtil.class);
    
    static final GeoStoreClient geostoreClient1 = new GeoStoreClient();
//    static GeoStoreClient geostoreClient2 = new GeoStoreClient();

    static String smallStat = " <statisticConfiguration>"
            + "   <name>area_admin_small</name>"
            + "   <title>Low resolution admin areas</title>"
            + "   <description>Compute the area for the administrative areas. Low resolutions raster.</description>"
            + "   <topic>rdc</topic>"
            + "   <topic>area</topic>"
            + "   <topic>administartive</topic>"
            + "   <stats>"
            + "      <stat>SUM</stat>"
            + "      <stat>MIN</stat>"
            + "      <stat>MAX</stat>"
            + "      <stat>COUNT</stat>"
            + "   </stats>"
            + "   <deferredMode>false</deferredMode>"
            + "   <dataLayer>"
            + "      <file>src/test/resources/rdc_admin_area2.tif</file>"
            + "   </dataLayer>"
            + "   <classificationLayer zonal=\"true\">"
            + "      <file>{rasterfile}</file>"
            + "      <nodata>65535</nodata>"
            + "   </classificationLayer>"
            + "   <output>"
            + "      <format>CSV</format>"
            + "      <separator>;</separator>"
            + "      <NaNValue>-1</NaNValue>"
            + "   </output>"
            + "</statisticConfiguration>";

    static public void init(GeoStoreConfig geoStoreConfig) {
        LOGGER.info("Initializing " + UNREDDGeoStoreTestUtil.class.getSimpleName());

        LOGGER.info("Init geostoreclient @ " + geoStoreConfig.getUrl());
        geostoreClient1.setGeostoreRestUrl(geoStoreConfig.getUrl());
        geostoreClient1.setUsername(geoStoreConfig.getUsername());
        geostoreClient1.setPassword(geoStoreConfig.getPassword());
    }


    public static void loadDefaultData() {
//        geostoreClient2.setGeostoreRestUrl(gsturld);
//        geostoreClient2.setUsername(gstuserd);
//        geostoreClient2.setPassword(gstpwdd);


        UNREDDLayer layer1 = new UNREDDLayer();

        layer1.setAttribute(UNREDDLayer.Attributes.RASTERPIXELWIDTH, "3876");
        layer1.setAttribute(UNREDDLayer.Attributes.RASTERPIXELHEIGHT, "3562");
        layer1.setAttribute(UNREDDLayer.Attributes.RASTERX0, "-50");
        layer1.setAttribute(UNREDDLayer.Attributes.RASTERX1, "50");
        layer1.setAttribute(UNREDDLayer.Attributes.RASTERY0, "-50");
        layer1.setAttribute(UNREDDLayer.Attributes.RASTERY1, "50");
        layer1.setAttribute(UNREDDLayer.Attributes.RASTERATTRIBNAME, "scene_id");
        layer1.setAttribute(UNREDDLayer.Attributes.MOSAICPATH, "layer1");
        layer1.setAttribute(UNREDDLayer.Attributes.RASTERNODATA, "0.0");

        
        List<RESTResource> resourceList = new ArrayList<RESTResource>();
        resourceList.add(UNREDDResourceBuilder.createLayerResource("layer1", layer1.createRESTResource().getAttribute()));
        resourceList.add(UNREDDResourceBuilder.createLayerResource("layer2", null));
        resourceList.add(UNREDDResourceBuilder.createLayerResource("layer3", null));

        resourceList.add(UNREDDResourceBuilder.createLayerUpdateResource("layer1", "2009", "12", null, null));
        resourceList.add(UNREDDResourceBuilder.createLayerUpdateResource("layer1", "2010", null, null, null));
        resourceList.add(UNREDDResourceBuilder.createLayerUpdateResource("layer1", "2011", null, null, null));

        resourceList.add(UNREDDResourceBuilder.createLayerUpdateResource("layer2", "2010", null, null, null));
        resourceList.add(UNREDDResourceBuilder.createLayerUpdateResource("layer2", "2011", null, null, null));

        resourceList.add(UNREDDResourceBuilder.createLayerUpdateResource("layer3", "2009", null, null, null));
        resourceList.add(UNREDDResourceBuilder.createLayerUpdateResource("layer3", "2010", "11", null, null));
        resourceList.add(UNREDDResourceBuilder.createLayerUpdateResource("layer3", "2011", null, null, null));

        resourceList.add(UNREDDResourceBuilder.createStatsDefResource("SMALLSTAT2", smallStat, "layer1", "layer2", "layer3"));
        resourceList.add(UNREDDResourceBuilder.createStatsDefResource("CLASSSTAT2", smallStat, "layer1", "layer2", "layer3"));

        resourceList.add(UNREDDResourceBuilder.createChartScriptResource("script1", "SMALLSTAT2", "src/test/resources/configuration/script/script.groovy"));
        resourceList.add(UNREDDResourceBuilder.createChartScriptResource("script2", "SMALLSTAT2", "src/test/resources/configuration/script/script.groovy"));
        resourceList.add(UNREDDResourceBuilder.createChartScriptResource("script3", "CLASSSTAT2", "src/test/resources/configuration/script/script.groovy"));

        LOGGER.info("Inserting test data in GeoStore...");

        for (RESTResource resource : resourceList) {
            geostoreClient1.insert(resource);
//            geostoreClient2.insert(resource);

        }

    }

    public static Long insertStatsDef(String statsdefname, String content, String layer) {
        RESTResource statsDefRRes = UNREDDResourceBuilder.createStatsDefResource(statsdefname, content, layer);
        return geostoreClient1.insert(statsDefRRes);
    }

    public static void insertLayerUpdate(String layer, String year, String month, String day) {       
        RESTResource rs = UNREDDResourceBuilder.createLayerUpdateResource(layer, year, month, day, null);
        geostoreClient1.insert(rs);
    }

    public static Long insertStatsData(String statsdef, String year, String month, String day, String content) {
        RESTResource rs = UNREDDResourceBuilder.createStatsDataResource(statsdef, year, month, day, content);
        return geostoreClient1.insert(rs);
    }

    public static void deleteAllResources() {
        ShortResourceList sres = geostoreClient1.searchResources(new FieldFilter(BaseField.NAME, "*", SearchOperator.IS_NOT_NULL));
        if( ! sres.isEmpty() ) {
            LOGGER.info("DeleteAll: deleting " + sres.getList().size() + " resources");
            for (ShortResource shortResource : sres.getList()) {
                LOGGER.info("DeleteAll: deleting resource #"+shortResource.getId()+ " " + shortResource.getName());
                geostoreClient1.deleteResource(shortResource.getId());
            }
        }
    }

    public static void deleteResources(UNREDDCategories cat) {
        LOGGER.info("Removing all resources in category " + cat);
        SearchFilter  filter = new CategoryFilter(cat.getName(), SearchOperator.EQUAL_TO);
        ShortResourceList sres = geostoreClient1.searchResources(filter);
        if( ! sres.isEmpty() ) {
            LOGGER.info("Deleting " + sres.getList().size() + " resources");
            for (ShortResource shortResource : sres.getList()) {
                LOGGER.info("Deleting resource #"+shortResource.getId()+ " " + shortResource.getName());
                geostoreClient1.deleteResource(shortResource.getId());
            }
        }
    }


}