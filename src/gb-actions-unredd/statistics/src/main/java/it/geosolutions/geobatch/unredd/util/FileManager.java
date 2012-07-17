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

package it.geosolutions.geobatch.unredd.util;

import java.io.File;
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
	
}
