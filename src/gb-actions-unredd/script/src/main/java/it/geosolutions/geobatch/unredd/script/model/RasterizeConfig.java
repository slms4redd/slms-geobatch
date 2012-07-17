/*
 *  Copyright (C) 2007 - 2012 GeoSolutions S.A.S.
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
package it.geosolutions.geobatch.unredd.script.model;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RasterizeConfig implements Cloneable {

    private String executable;
    private String taskExecutorXslFileName;
    private String freeMarkerTemplate;

    
    public String getExecutable() {
        return executable;
    }

    public void setExecutable(String executable) {
        this.executable = executable;
    }

    public String getFreeMarkerTemplate() {
        return freeMarkerTemplate;
    }

    public void setFreeMarkerTemplate(String freeMarkerTemplate) {
        this.freeMarkerTemplate = freeMarkerTemplate;
    }

    public String getTaskExecutorXslFileName() {
        return taskExecutorXslFileName;
    }

    public void setTaskExecutorXslFileName(String taskExecutorXslFileName) {
        this.taskExecutorXslFileName = taskExecutorXslFileName;
    }

    /**
     * Just get rid of CloneNotSupportedException
     */
    @Override
    public RasterizeConfig clone() {
        try {
            return (RasterizeConfig)super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException("Unexpected exception", ex);
        }
    }

}
