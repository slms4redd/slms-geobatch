    
import it.geosolutions.geostore.core.model.Resource;
import it.geosolutions.geostore.services.dto.search.CategoryFilter;
import it.geosolutions.geostore.services.dto.search.SearchFilter;
import it.geosolutions.geostore.services.dto.search.SearchOperator;
import it.geosolutions.geostore.services.dto.ShortResource;
import it.geosolutions.geostore.services.rest.model.ShortResourceList;
import it.geosolutions.geostore.services.rest.model.RESTResource;
import it.geosolutions.geostore.services.rest.model.RESTCategory;
import it.geosolutions.geostore.services.rest.GeoStoreClient;

import it.geosolutions.unredd.geostore.model.UNREDDCategories;
import it.geosolutions.unredd.geostore.model.UNREDDChartData;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

    
    public Map execute(Map argsMap) throws Exception {
        System.out.println("==== Running test script...");

        String name = argsMap.get("configuration").getProperties().get("chartscript_name");
        System.out.println("==== Running test script " + name);

        GeoStoreClient client = createGeoStore(argsMap);
        int catCnt = client.getCategories().getList().size();
        System.out.println("=== Script found " + catCnt + " categories");

        SearchFilter statsDataFilter = new CategoryFilter(UNREDDCategories.STATSDATA.getName(), SearchOperator.EQUAL_TO);
        ShortResourceList allStatsData = client.searchResources(statsDataFilter);
        if (allStatsData.getList().isEmpty())
            throw new IllegalStateException("Some statsdata are expected while testing");
        else
            System.out.println("= Found " + allStatsData.getList().size() + " StatsData");

        final List retData = new ArrayList();

        for (ShortResource r : allStatsData.getList()) {
            System.out.println("= Processing " + r.getName());
            Resource sdata = client.getResource(r.getId());
            String   data = client.getData(r.getId());

            String chartName = "TESTCHARTDATA_" + r.getName();

            UNREDDChartData ucd = new UNREDDChartData();
            ucd.setAttribute(UNREDDChartData.Attributes.CHARTSCRIPT, name);
            RESTResource newChartData = ucd.createRESTResource();
            newChartData.setName(chartName);
            newChartData.setData("ADDEDVALUE_" + data);

//            RESTResource newChartData = new RESTResource();
//            newChartData.setCategory(new RESTCategory(UNREDDCategories.CHARTDATA.getName()));
//            newChartData.setName(chartName);
//            newChartData.setData("ADDEDVALUE_" + data);
            Long id = client.insert(newChartData);
            retData.add(chartName); // sample output, not really needed
            System.out.println("=== Created ChartData '"+newChartData.getName()+"' with ID  " + id );
        }

        // sample results
        Map ret = new HashMap();
        ret.put("return", retData);
        return ret;
}


GeoStoreClient createGeoStore(Map argsMap) {
        Map props = argsMap.get("configuration").getProperties();

        System.out.println("geostore url: " + props.get("geostore_url"));

        String gurl = props.get("geostore_url");
        String guser = props.get("geostore_username");
        String gpw = props.get("geostore_password");

        GeoStoreClient client = new GeoStoreClient();
        client.setGeostoreRestUrl(gurl);
        client.setUsername(guser);
        client.setPassword(gpw);
        return client;
}