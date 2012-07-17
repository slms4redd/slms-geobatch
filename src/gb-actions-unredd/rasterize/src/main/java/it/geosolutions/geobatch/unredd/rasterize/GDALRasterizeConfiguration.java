/*
 *  GeoBatch - Open Source geospatial batch processing system
 *  http://code.google.com/p/geobatch/
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
package it.geosolutions.geobatch.unredd.rasterize;

import it.geosolutions.geobatch.catalog.Configuration;
import it.geosolutions.geobatch.configuration.event.action.ActionConfiguration;

import java.util.Map;

/**
 * 
 * @author Luca Paolino - luca.paolino@geo-solutions.it
 * 
 */
public class GDALRasterizeConfiguration extends ActionConfiguration implements Configuration {
	
	double rasterx0 =0.0d;
	double rasterx1 =0.0d;
	double rastery0 =0.0d;
	double rastery1 =0.0d;
	int rasterpixelwidth = 0;
	int rasterpixelheight = 0;
	String rasterAttribName=null;
	String rasterCqlFilter=null;
	double rasterNoData=0.0d;
	String layername =null;
	int retileH=0;
	int retileW=0;
	String filename="";
	
    public GDALRasterizeConfiguration(String id, String name, String description) {
        super(id, name, description);
        // TODO INITIALIZE MEMBERS
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getLayername() {
        return layername;
    }

    public void setLayername(String layername) {
        this.layername = layername;
    }

    public int getRetileH() {
        return retileH;
    }

    public void setRetileH(int retileH) {
        this.retileH = retileH;
    }

    public int getRetileW() {
        return retileW;
    }

    public void setRetileW(int retileW) {
        this.retileW = retileW;
    }

    public double getRasterx0() {
        return rasterx0;
    }

    public void setRasterx0(double rasterx0) {
        this.rasterx0 = rasterx0;
    }

    public double getRasterx1() {
        return rasterx1;
    }

    public void setRasterx1(double rasterx1) {
        this.rasterx1 = rasterx1;
    }

    public double getRastery0() {
        return rastery0;
    }

    public void setRastery0(double rastery0) {
        this.rastery0 = rastery0;
    }

    public double getRastery1() {
        return rastery1;
    }

    public void setRastery1(double rastery1) {
        this.rastery1 = rastery1;
    }

    public int getRasterpixelwidth() {
        return rasterpixelwidth;
    }

    public void setRasterpixelwidth(int rasterpixelwidth) {
        this.rasterpixelwidth = rasterpixelwidth;
    }

    public int getRasterpixelheight() {
        return rasterpixelheight;
    }

    public void setRasterpixelheight(int rasterpixelheight) {
        this.rasterpixelheight = rasterpixelheight;
    }

    public String getRasterAttribName() {
        return rasterAttribName;
    }

    public void setRasterAttribName(String rasterAttribName) {
        this.rasterAttribName = rasterAttribName;
    }

    public String getRasterCqlFilter() {
        return rasterCqlFilter;
    }

    public void setRasterCqlFilter(String rasterCqlFilter) {
        this.rasterCqlFilter = rasterCqlFilter;
    }

    public double getRasterNoData() {
        return rasterNoData;
    }

    public void setRasterNoData(double rasterNoData) {
        this.rasterNoData = rasterNoData;
    }

    @Override
    public GDALRasterizeConfiguration clone() {
        final GDALRasterizeConfiguration ret = new GDALRasterizeConfiguration(this.getId(), this.getName(), this.getDescription());

        // TODO CLONE YOUR MEMBERS
        ret.setFilename(filename);
        ret.setRasterAttribName(rasterAttribName);
        ret.setRasterCqlFilter(rasterCqlFilter);
        ret.setRasterNoData(rasterNoData);
        ret.setRasterpixelheight(rasterpixelheight);
        ret.setRasterpixelwidth(rasterpixelwidth);
        ret.setRasterx0(rasterx0);
        ret.setRasterx1(rasterx1);
        ret.setRastery0(rastery0);
        ret.setRastery1(rastery1);
        ret.setLayername(layername);
        ret.setRetileH(retileH);
        ret.setRetileW(retileW);
        ret.setWorkingDirectory(this.getWorkingDirectory());
        ret.setServiceID(this.getServiceID());
        ret.setListenerConfigurations(ret.getListenerConfigurations());
        return ret;
    }

    @Override
    public String toString() {
        return "GDALRasterizeConfiguration{" 
                + "rasterx0=" + rasterx0 + ", rasterx1=" + rasterx1 +
                ", rastery0=" + rastery0 + ", rastery1=" + rastery1 +
                ", rasterpixelwidth=" + rasterpixelwidth +
                ", rasterpixelheight=" + rasterpixelheight +
                ", rasterAttribName=" + rasterAttribName +
                ", rasterCqlFilter=" + rasterCqlFilter +
                ", rasterNoData=" + rasterNoData +
                ", layername=" + layername +
                ", retileH=" + retileH + ", retileW=" + retileW +
                '}';
    }
}
