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


import it.geosolutions.geobatch.unredd.script.util.FlowUtil;
import it.geosolutions.unredd.geostore.model.UNREDDLayerUpdate;
import it.geosolutions.unredd.geostore.model.UNREDDStatsDef;
import it.geosolutions.geostore.services.dto.ShortAttribute;

import it.geosolutions.geostore.services.rest.model.RESTResource;
import it.geosolutions.unredd.geostore.model.UNREDDChartScript;
import it.geosolutions.unredd.geostore.model.UNREDDChartScript.ReverseAttributes;
import it.geosolutions.unredd.geostore.model.UNREDDLayer;
import it.geosolutions.unredd.geostore.model.UNREDDStatsData;
import it.geosolutions.unredd.geostore.utils.NameUtils;

import java.util.List;

public class UNREDDResourceBuilder{

	/*******
	 * create a RESTResource in the form of UNREDDLayerUpdate
	 * @param name
	 * @param year
	 * @param month
	 * @return
     * @deprecated also add the content please
	 */
	public  static RESTResource createLayerUpdateResource(String name, String year, String month){
        return createLayerUpdateResource(name, year, month, null, null);
	}

    
	public static RESTResource createLayerUpdateResource(String name, String year, String month, String day, String content){

        UNREDDLayerUpdate layerUpdate = new UNREDDLayerUpdate();

        layerUpdate.setAttribute(UNREDDLayerUpdate.Attributes.LAYER, name);
        layerUpdate.setAttribute(UNREDDLayerUpdate.Attributes.YEAR, year);
		if (month!=null) {
            layerUpdate.setAttribute(UNREDDLayerUpdate.Attributes.MONTH, month);
        }

        RESTResource res = layerUpdate.createRESTResource();
		res.setName(NameUtils.buildLayerUpdateName(name, year, month, day));
        res.setData(content);

        return res;
	}

	/*********
	 * create a RESTResource in the form of layer
	 * @param name
	 * @param url
	 * @param path
	 * @return
	 */
	public  static RESTResource createLayerResource(String name, List<ShortAttribute> optionalAttributes){
        UNREDDLayer layer = new UNREDDLayer();
//        layer.setAttribute(Attributes.DESTRELATIVEPATH, path);

        RESTResource res = layer.createRESTResource();
		res.setName(name);

		if (optionalAttributes!=null) {
            res.getAttribute().addAll(optionalAttributes);
		}

        return res;
	}

	/**********
	 * create a RESTResource int he form of statsdef
	 * @param name
	 * @param xml
	 * @param layers
	 * @return
	 */
	public static RESTResource createStatsDefResource(String name, String xml, String... layers) {

        UNREDDStatsDef statsDef = new UNREDDStatsDef();

        statsDef.addReverseAttribute(UNREDDStatsDef.ReverseAttributes.LAYER, layers);

        RESTResource res = statsDef.createRESTResource();
        res.setName(name);
        res.setData(xml);

        return res;
	}

	public  static RESTResource createStatsDataResource(String statsDefName, String year, String month, String day, String content){

        UNREDDStatsData statsData = new UNREDDStatsData();

        statsData.setAttribute(UNREDDStatsData.Attributes.STATSDEF, statsDefName);
        statsData.setAttribute(UNREDDStatsData.Attributes.YEAR, year);
		if (month!=null) {
            statsData.setAttribute(UNREDDStatsData.Attributes.MONTH, month);
        }

        RESTResource res = statsData.createRESTResource();
		res.setName(NameUtils.buildStatsDataName(statsDefName, year, month, day));
        res.setData(content);
        
        return res;
	}


	public  static RESTResource createChartScriptResource(String name, String statsdef, String script){

        UNREDDChartScript chartScript = new UNREDDChartScript();
        chartScript.setAttribute(UNREDDChartScript.Attributes.SCRIPTPATH, script);
        chartScript.addReverseAttribute(ReverseAttributes.STATSDEF, statsdef);

		RESTResource res = chartScript.createRESTResource();
		res.setName(name);

		return res;
	}

//	public static SearchFilter createSearchByLayerAndCategory(String layerName, UNREDDCategories category) {
//		SearchFilter filter = new AndFilter(
//				new FieldFilter(BaseField.NAME, "%", SearchOperator.LIKE) ,
//				new CategoryFilter(category.getName(), SearchOperator.EQUAL_TO),
//				new AttributeFilter(layerName,
//                                    UNREDDStatsDef.ReverseAttributes.LAYER.getName(),
//                                    UNREDDStatsDef.ReverseAttributes.LAYER.getType(),
//                                    SearchOperator.EQUAL_TO)
//				);
//		return filter;
//	}


}
