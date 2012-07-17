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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class PostGisConfig implements Cloneable {
    private String host;
    private int port;
    private String database;
    private String schema;
    private String username;
    private String password;

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String buildOGRString() {
	    return "PG:\"host="+host+" port="+port+" dbname="+database+" schemas="+schema+" user="+username+" password="+password+"\"";
//	    return "PG:\"host="+host+" port="+port+" dbname="+database+" user="+username+" password="+password+"\"";
    }

    public Map buildGeoToolsMap() {
        Map map = new HashMap();
        
        map.put("dbtype", "postgis");   // must be postgis
        map.put("host", host);          // the name or ip address of the machine running PostGISUtils
        map.put("port", port);          // the port that PostGISUtils is running on (generally 5432)
        map.put("database", database);  // the name of the database to connect to.
        map.put("schema", schema);
        map.put("user", username);      // the user to connect with
        map.put("passwd", password);

        return map;
    }


    @Override
    public String toString() {
        return "PostGisConfig[" + username+ "@" + host + ":" + port + "/" + database + "/" + schema + ']';
    }

    /**
     * Just get rid of CloneNotSupportedException
     */
    @Override
    public PostGisConfig clone() {
        try {
            return (PostGisConfig)super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException("Unexpected exception", ex);
        }
    }

}
