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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBTestUtil {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(DBTestUtil.class);
    
    public static void deleteTable(String db, String schema, String table, String user, String pwd) throws SQLException, ClassNotFoundException{
        Connection connection = null;
        try
        {
            // This is the JDBC driver class for Oracle database
            Class.forName("org.postgresql.Driver");

            // We use an Oracle express database for this example
            String url = "jdbc:postgresql://localhost:5432/"+db;

            // Define the username and password for connection to
            // our database.
            String username = user;
            String password = pwd;

            // To delete a table from database we use the DROP TABLE
            // command and specify the table name to be dropped
      
            String sql2 = "delete from "+schema+"."+table;
            // Connect to database
            connection = DriverManager.getConnection(url, username, password);
            // Create a statement
            Statement statement = connection.createStatement();
            // Execute the statement to delete the table
            statement.execute(sql2);
        } catch (SQLException e) {
            LOGGER.warn(" the content of the table "+table+" cannot be deleted. Probaly it does not exist");
        } finally {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
    }
    
    public static String getMosaicDBEntry(String db, String schema, String layer, String location, String user, String pwd) throws SQLException, ClassNotFoundException{
        Connection connection = null;
        try
        {
            // This is the JDBC driver class for Oracle database
            Class.forName("org.postgresql.Driver");

            // We use an Oracle express database for this example
            String url = "jdbc:postgresql://localhost:5432/"+db;

            // Define the username and password for connection to
            // our database.
            String username = user;
            String password = pwd;

            // To delete a table from database we use the DROP TABLE
            // command and specify the table name to be dropped
      
            String sql2 = "select * from "+schema+"."+layer+" where location='"+location+"'";
            // Connect to database
            connection = DriverManager.getConnection(url, username, password);
            // Create a statement
            Statement statement = connection.createStatement();
            // Execute the statement to delete the table
            ResultSet resultset=statement.executeQuery(sql2);
            resultset.next();
            return resultset.getString("location");
        } catch (SQLException e) {
            LOGGER.warn(" Cannot perform the selection "+layer+" ",e);
            return null;
        } finally {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
    }
    
    
    
    public static void deleteGeometryReference(String db, String layer, String user, String pwd) throws SQLException, ClassNotFoundException{
        LOGGER.info("deleting geometry reference of "+layer+" in "+db);
        Connection connection = null;
        try
        {
            // This is the JDBC driver class for Oracle database
            Class.forName("org.postgresql.Driver");

            // We use an Oracle express database for this example
            String url = "jdbc:postgresql://localhost:5432/"+db;

            // Define the username and password for connection to
            // our database.
            String username = user;
            String password = pwd;

            // To delete a table from database we use the DROP TABLE
            // command and specify the table name to be dropped
      
            String sql2 = "delete from geometry_columns where f_table_name='"+layer+"'";
            // Connect to database
            connection = DriverManager.getConnection(url, username, password);
            // Create a statement
            Statement statement = connection.createStatement();
            // Execute the statement to delete the table
            statement.execute(sql2);
            connection.commit();
        } catch (SQLException e) {
            
            LOGGER.warn(" Cannot delete the "+layer+" item in the geometry_columns table. Probaly it does not exist");
        } finally {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
    }
    
    public static void droptable(String db, String schema, String table, String user, String pwd) throws SQLException, ClassNotFoundException{
        Connection connection = null;
        try
        {
            // This is the JDBC driver class for Oracle database
            Class.forName("org.postgresql.Driver");

            // We use an Oracle express database for this example
            String url = "jdbc:postgresql://localhost:5432/"+db;

            // Define the username and password for connection to
            // our database.
            String username = user;
            String password = pwd;

            // To delete a table from database we use the DROP TABLE
            // command and specify the table name to be dropped
            String sql = "DROP TABLE "+schema+"."+table+";";        

            // Connect to database
            connection = DriverManager.getConnection(url, username, password);
            // Create a statement
            Statement statement = connection.createStatement();
            // Execute the statement to delete the table

            statement.execute(sql);
            connection.commit();
        } catch (SQLException e) {
            LOGGER.warn("The table "+table+" cannot be dropped in "+db+". Probably, it does not exist");
        } finally {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
    }
 
}
