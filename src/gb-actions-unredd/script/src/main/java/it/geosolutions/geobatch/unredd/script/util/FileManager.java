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

package it.geosolutions.geobatch.unredd.script.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @deprecated use apache FileUtils directly
 */
public class FileManager {
    private final static Logger LOGGER = LoggerFactory.getLogger(FileManager.class);	
    /**
     * @deprecated use apache FileUtils directly
     */
	public static String copyfile(String destDir,  String destFileName, String srcFileName ){
        File source = new File(srcFileName);
        File dest = new File(destDir,destFileName);
        try {
            FileUtils.copyFile(source, dest);
            LOGGER.info("File copied: " + source + " --> " + dest);
        } catch (IOException ex) {
            LOGGER.warn("Error copying file: " + source + " --> " + dest, ex);
            return null;
        }
        // TODO: check original return line
        return dest.toString();
	}
	
    /**
    // Deletes all files and subdirectories under dir.
    // Returns true if all deletions were successful.
    // If a deletion fails, the method stops attempting to delete and returns false.
     * @deprecated use apache FileUtils directly
     */
    public static boolean deleteDir(File dir) {
        try {
            FileUtils.deleteDirectory(dir);
            return true;
        } catch (IOException ex) {
            LOGGER.warn("Error removing dir: " + dir, ex);
        }
        return false;
    } 
    
    /**
     * Fetch the entire contents of a text file, and return it in a String.
     * This style of implementation does not throw Exceptions to the caller.
     *
     * @param aFile is a file which already exists and can be read.
     */
     public static String getContents(File aFile) {
       //...checks on aFile are elided
       StringBuilder contents = new StringBuilder();
       
       try {
         //use buffering, reading one line at a time
         //FileReader always assumes default encoding is OK!
         BufferedReader input =  new BufferedReader(new FileReader(aFile));
         try {
           String line = null; //not declared within while loop
           /*
           * readLine is a bit quirky :
           * it returns the content of a line MINUS the newline.
           * it returns null only for the END of the stream.
           * it returns an empty String if two newlines appear in a row.
           */
           while (( line = input.readLine()) != null){
             contents.append(line);
             contents.append(System.getProperty("line.separator"));
           }
         }
         finally {
           input.close();
         }
       }
       catch (IOException ex){
         ex.printStackTrace();
       }
       
       return contents.toString();
     }
     
     public static String mkUNREDDdir(String basepath, String layer, String year, String month) throws IOException {
         String path = basepath+System.getProperty("file.separator")+layer+System.getProperty("file.separator")+year;
         if (month!=null) path+=(System.getProperty("file.separator")+month);
         File directory = new File(path);
         FileUtils.forceMkdir(directory);
         return path;
     }
	
}
