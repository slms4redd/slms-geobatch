import groovy.text.SimpleTemplateEngine
import java.util.Map
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.IOUtils
import it.geosolutions.unredd.geostore.model.UNREDDStatsData
import it.geosolutions.unredd.geostore.model.UNREDDChartData
import it.geosolutions.unredd.geostore.UNREDDGeostoreManager
import it.geosolutions.geostore.services.rest.model.RESTResource
import it.geosolutions.geostore.services.rest.GeoStoreClient
import it.geosolutions.geostore.services.rest.model.RESTStoredData
import it.geosolutions.geostore.core.model.Resource
import it.geosolutions.geostore.core.model.Attribute


log = LoggerFactory.getLogger("training_stats.groovy");

println "_TEST_"
log.info "_TEST_"

htmlTemplateFilePath = '/var/geobatch/config/chartscripts/deforestation_chart_template.html' // DEBUG
prefix               = 'admin1' // DEBUG
langFilePath         = '/var/geobatch/config/chartscripts/lang.csv' // DEBUG

//chartScriptName = 'deforestation_script'

csvSeparator = ';'


// The following lines are used to test the script
// Comment out them for deploying with GeoBatch
class Conf {
    def geostore_url
    def geostore_username
    def geostore_password
    def chartscript_name
}
def argsMap = [
    configuration: new Conf(
        geostore_url:      "http://127.0.0.1:9191/geostore/rest",
        geostore_username: "admin",
        geostore_password: "admin",
        chartscript_name:  "deforestation_script"
    )
];
//execute(argsMap)
// Comment out up to here

def execute(Map argsMap)
{
    log.info "Starting chart script"
    //println "ecchice"

    GeoStoreClient client = createGeoStore(argsMap);
    UNREDDGeostoreManager manager = new UNREDDGeostoreManager(client)
    
    chartScriptName = argsMap.get("configuration").getProperties().get("chartscript_name");
    
    // Load forest change forest data
    forestData = importData(manager, chartScriptName, true)
    nonForestData = importData(manager, chartScriptName, false)
    
    // Load localized data
    loc = loadLangData(langFilePath)
    
    def engine = new SimpleTemplateEngine()
    def htmlTemplate = engine.createTemplate(new File(htmlTemplateFilePath));
    //def csvTemplate  = engine.createTemplate(new File(csvTemplateFilePath));
        
    def lastYear  = null;
    def firstYear = null;
    
    deletePreviousChartData(client, chartScriptName);
    
    // Iterate through the languages
    loc.each { langKey, loc ->
        // Create one HTML per row in the forest change stats data
        forestData.each() { featureId, row ->
            try {
                def years = row.keySet();
                if (lastYear  == null) lastYear  = years.max()
                if (firstYear == null) firstYear = years.min()

                def binding = ['loc': loc, 'forest': row, 'nonForest': nonForestData[featureId], 'lastYear': lastYear, 'firstYear': firstYear]
                html = htmlTemplate.make(binding)

                // DEBUG: Save as file
                //polygonId = deforestationValues[0] as int;
                //outputFilePath = outputBaseDir + '/' + langKey + '/' + prefix + '/' + prefix + '_' + featureId + '.html'
                //saveAsFile(html, outputFilePath)
                //saveAsFile(html, '/Users/sgiaccio/stats/' + featureId + '.html') // DEBUG
                
                // Save in GeoStore
                def resourceName = chartScriptName + "_" + featureId + "_" + langKey
                id = saveOnGeoStore(client, featureId, resourceName, html.toString(), "deforestation_script", false, langKey, "html")
                log.info "GeoStore resource saved: Resource Name = " + resourceName + " - ID = " + id
            } catch (Exception e) {
                log.info("Problem encountered in creating chart for featureId = " + featureId)
                e.printStackTrace();
            }
        }
        
        // Create csv
        //def csvBinding = ['loc': loc, 'forestChange': forestChangeStatsData]
        //def csv = csvTemplate.make(csvBinding)
        //println csv; // DEBUG
    }
    
    return ["return": []]
}

def loadLangData(langFilePath)
{
    langFile = new File(langFilePath)
    lines = langFile.readLines()
    headerLine = lines.head().split("\t")
    languages = headerLine.tail() // remove first column from first line (it's not a language code nor a label id)
    
    // fill the loc hash map
    loc = [:]
    lines = lines.tail()
    lines.each { row ->
        splitRow = row.split("\t")
        key = splitRow.head()
        splitRow = splitRow.tail()
        splitRow.eachWithIndex { column, i ->
            values = loc[languages[i]]
            if (values == null) {
                loc[languages[i]] = [:]
                loc[languages[i]][key] = column
            } else {
                values[key] = column //.strip()
            }
        }
    }
    
    return loc
}

def deletePreviousChartData(client, chartScriptName) {
    UNREDDGeostoreManager manager = new UNREDDGeostoreManager(client)

    // Search all chart data for the given chart script
    List data = manager.searchChartDataByChartScript(chartScriptName)

    // Delete the chart data found
    for (chartData in data)
    {
        def id = chartData.getId();
        log.info("Resource deleted - id = " + id)
        
        client.deleteResource(id);
    }
}

def saveOnGeoStore(client, featureId, name, html, chartScriptName, published, language, format)
{
    // Create the UNREDDChartData object and fill the atributes
    unreddChartData = new UNREDDChartData()
    unreddChartData.setAttribute(UNREDDChartData.Attributes.CHARTSCRIPT, chartScriptName)
    unreddChartData.setAttribute(UNREDDChartData.Attributes.PUBLISHED, published ? "true" : "false")
    unreddChartData.setAttribute(UNREDDChartData.Attributes.FEATUREID, featureId + "")

    // These attributes are not used yet
    //unreddChartData.setAttribute(UNREDDChartData.Attributes.LANGUAGE, language)
    //unreddChartData.setAttribute(UNREDDChartData.Attributes.FORMAT, format)

    // Create the RESTResource and set the name
    RESTResource chartDataRestResource = unreddChartData.createRESTResource()
    chartDataRestResource.setName(name)

    // Set the data to be stored
    RESTStoredData rsd = new RESTStoredData()
    rsd.setData(html)
    chartDataRestResource.setStore(rsd)
    
    // Insert in GeoStore
    int id = client.insert(chartDataRestResource)
    return id
}

// Loads the stats data from GeoStore
def importData(manager, dataId, forest)
{
    List resources = manager.searchStatsDataByStatsDef2('deforestation'); // dataId)
    
    Map output = new HashMap()
    
    for (Resource resource : resources)
    {
        data = resource.getData()
        
        int year  = (Float.parseFloat(getAttribute(resource.getAttribute(), UNREDDStatsData.Attributes.YEAR).getValue())).trunc()
        def lines = parseTable(data.getData(), forest)
        lines.each { id, line ->
            temp = output[id]
            if (temp == null) {
                output[id] = new TreeMap()
                output[id][year] = line
            } else {
                temp[year] = line
            }
        }
    }
    
    return output
}


def fillNullRows(map) {
    print " ----- " + map.get(map.keySet().min());
    return null;
}

// Parses a CSV table - returns a HashMap where the key is the polygon ID (first column in the CSV)
// and the value is the full parsed row (stored as an array)
def parseTable(table, forest)
{
    HashMap lines = new HashMap()
    
    table.eachLine { line ->
        parsedArr = []
        arr = line.tokenize(csvSeparator)
        
        if ("0".equals(arr[1]) && forest || "1".equals(arr[1]) && !forest)
            return false // skip loop
        
        polygonId = Integer.parseInt(arr[0])
        
        // Values for each administrative regions are split in two rows (forest and non-forest) - join them together again
        for (i in 2..<arr.size) // first element in array is the polygon id, don't need it
        {
            //println 'i = ' + i
            //println 'Double.parseDouble(arr[i]) = ' + Double.parseDouble(arr[i])
            //println 'parsedArr[i - 2] = ' + parsedArr[i - 2]
            parsedArr[i - 2] = Double.parseDouble(arr[i])
        }
        
        //println 'parsedArr = ' + parsedArr
        lines.put(polygonId, parsedArr)
    }
    
    //print lines
    return lines
}

def getAttribute(List attributeList, attribute)
{
    for (Attribute attr : attributeList) {
        if (attr.getName().equals(attribute.getName())) {
            return attr
        }
    }
    
    return null
}

def saveAsFile(html, outputFilePath)
{
    log.info("Saving output to file: " + outputFilePath);
    out = new File(outputFilePath)
    out.write(html.toString(), "UTF-8")
}

GeoStoreClient createGeoStore(Map argsMap) {
    Map props = argsMap.get("configuration").getProperties();

    log.info("geostore url: " + props.get("geostore_url"));

    String gurl  = props.get("geostore_url");
    String guser = props.get("geostore_username");
    String gpw   = props.get("geostore_password");

    GeoStoreClient client = new GeoStoreClient();
    client.setGeostoreRestUrl(gurl);
    client.setUsername(guser);
    client.setPassword(gpw);
    return client;
}
