package it.geosolutions.geobatch.unredd.script.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.geotools.data.DataUtilities;
import org.geotools.gce.imagemosaic.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An Helper class responsible to check if the mosaic dir exist and in case create it with the standard UNREDD indexer.properties and timeregex.properties files
 * 
 * See http://docs.geoserver.org/latest/en/user/tutorials/imagemosaic_timeseries/imagemosaic_timeseries.html for more information about the usage of geoserver imagemosaic timeseries
 * 
 * @author DamianoG
 *
 */
public class MosaicDirBuilder {

	
	protected final static Logger LOGGER = LoggerFactory.getLogger(MosaicDirBuilder.class);
	
	/**
	 * Build a mosaic dir if the provided one not exists.
	 * The method create also the indexer.properties and timeregex.properties if they not exists
	 * 
	 * @param mosaicDir
	 * @param cfg
	 * @param timeRegex
	 * @throws NullPointerException
	 * @throws IOException
	 */
	public static void buildMosaicDir(File mosaicDir, String tmpIndexerPath, String timeRegex) throws NullPointerException, IOException {
		
		try{
			if(!mosaicDir.exists()){
	        	LOGGER.info("the mosaicDir '" + mosaicDir + "' doesn't exist, going to create it and try to configure the indexer.properties...");
	        	if(!mosaicDir.mkdir()){
	        		throw new IOException("The directory '" + mosaicDir + "' doesn't exist and CANNOT BE CREATED... create it or check the layer configuration on geostore");
	        	}
		        if(tmpIndexerPath != null){
	        		File indexerProperties = new File(tmpIndexerPath);
		    		if(!indexerProperties.exists() || !indexerProperties.canRead()){
		    			LOGGER.warn("The indexer.properties cannot be created automagically from a default UNREDD one... the flow is going to continue its executions but a GEOBATCH default indexer.properties without time dimension will be created...");
		    		}
		    		else{
			    		try{
			    			FileUtils.copyFileToDirectory(indexerProperties, mosaicDir);
			    			LOGGER.info("The default UNREDD indexer.properties has been copyied properly in the mosaic dir...");
			    		}
			    		catch(Exception e){
			    			LOGGER.warn("The default UNREDD indexer.properties cannot be copied in the mosaicDirectory... the flow is going to continue its executions but a GEOBATCH default indexer.properties without time dimension will be created...");
			    		}
		    		}
	        	}
		        else{
		        	LOGGER.info("The configuration doesn't specify any default indexer file... ");
		        }
	        }
			
			createRegexFile(new File(mosaicDir, "timeregex.properties"), timeRegex);
		}
		finally{
			boolean outcome = true;
			File indexer = new File(mosaicDir, "indexer.properties");
			File timeregex = new File(mosaicDir, "timeregex.properties");
			if(indexer==null || timeregex==null || !indexer.exists() || !timeregex.exists()){
				outcome=false;
			}
			if(!outcome){
				mosaicDir.delete();
				throw new IOException("the indexer or the timeregex hasn't been created due to an error, the mosaic dir has been removed. Check your system configuraton.");
			}
		}
	}
	
    /**
     * If the regex file do not exists, build it using the passed configuration and return the
     * corresponding properties object
     * 
     * @param regexFile
     * @param configuration
     * @return
     * @throws NullPointerException
     * @throws IOException 
     */
	private static Properties createRegexFile(File regexFile, String regex) throws NullPointerException, IOException {

        if (!regexFile.exists()) {
            FileWriter outFile = null;
            PrintWriter out = null;
            if (regex != null) {
                try {
                    outFile = new FileWriter(regexFile);
                    out = new PrintWriter(outFile);

                    // Write text to file
                    out.println("regex=" + regex);
                } catch (IOException e) {
                    if (LOGGER.isErrorEnabled())
                        LOGGER.error("Error occurred while writing " + regexFile.getAbsolutePath()
                                + " file!", e);
                } finally {
                    if (out != null) {
                        out.flush();
                        out.close();
                    }

                    outFile = null;
                    out = null;
                }
            } else
                throw new NullPointerException(
                        "Unable to build the property file using a null regex string");

            return getPropertyFile(regexFile);
        }
        return null;
    }
	
    /**
     * get the properties object from the file.
     * 
     * @param properties
     *            the file referring to the prop file to load
     * @return
     * @throws NullPointerException TODO
     * @throws IOException 
     */
    protected static Properties getPropertyFile(File properties) throws NullPointerException, IOException {
        URL url = DataUtilities.fileToURL(properties);
        Properties props = null;
        if (url != null) {
            props = Utils.loadPropertiesFromURL(url);
        } else {
            throw new NullPointerException("Unable to resolve the URL: "
                    + properties.getAbsolutePath());
        }

        return props;
    }
	
	
}
