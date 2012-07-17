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

package it.geosolutions.geobatch.unredd.script.util.rasterize;

import java.util.HashMap;
import java.util.Map;

public class GDALOptions {
    String layer;
    String src;
    
    String attribute;
    String ph;
    String pw;
    String rx0;
    String rx1;
    String ry0;
    String ry1;
    String nodata;
    String ot;
    Map<String, String> options = new HashMap<String, String>();
    String asrs;
    String where;
    String of ="GTiff";
    
    
    String tiled;
    String th;
    String tw;
    
    


    public String getTiled() {
        return tiled;
    }
    public void setTiled(String tiled) {
        this.tiled = tiled;
    }
    public String getTh() {
        return th;
    }
    public void setTh(String th) {
        this.th = th;
    }
    public String getTw() {
        return tw;
    }
    public void setTw(String tw) {
        this.tw = tw;
    }
    public Map<String, String> getOptions() {
        return options;
    }
    public void setOptions(Map<String, String> options) {
        this.options = options;
    }
    public String getOf() {
        return of;
    }
    public void setOf(String of) {
        this.of = of;
    }
    public String getWhere() {
        return where;
    }
    public void setWhere(String where) {
        this.where = where;
    }
    public String getSrc() {
        return src;
    }
    public void setSrc(String src) {
        this.src = src;
    }
    public String getLayer() {
        return layer;
    }
    public void setLayer(String layer) {
        this.layer = layer;
    }
    public String getAttribute() {
        return attribute;
    }
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
    public String getPh() {
        return ph;
    }
    public void setPh(String ph) {
        this.ph = ph;
    }
    public String getPw() {
        return pw;
    }
    public void setPw(String pw) {
        this.pw = pw;
    }
    public String getRx0() {
        return rx0;
    }
    public void setRx0(String rx0) {
        this.rx0 = rx0;
    }
    public String getRx1() {
        return rx1;
    }
    public void setRx1(String rx1) {
        this.rx1 = rx1;
    }
    public String getRy0() {
        return ry0;
    }
    public void setRy0(String ry0) {
        this.ry0 = ry0;
    }
    public String getRy1() {
        return ry1;
    }
    public void setRy1(String ry1) {
        this.ry1 = ry1;
    }
    public String getNodata() {
        return nodata;
    }
    public void setNodata(String nodata) {
        this.nodata = nodata;
    }
    public String getOt() {
        return ot;
    }
    public void setOt(String ot) {
        this.ot = ot;
    }
    public String getAsrs() {
        return asrs;
    }
    public void setAsrs(String asrs) {
        this.asrs = asrs;
    }
    

    
}
