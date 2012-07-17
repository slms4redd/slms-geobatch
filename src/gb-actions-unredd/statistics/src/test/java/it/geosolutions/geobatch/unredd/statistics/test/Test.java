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

package it.geosolutions.geobatch.unredd.statistics.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.EventObject;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import it.geosolutions.filesystemmonitor.monitor.FileSystemEvent;
import it.geosolutions.filesystemmonitor.monitor.FileSystemEventType;
import it.geosolutions.geobatch.flow.event.action.ActionException;
import it.geosolutions.geobatch.unredd.statistics.StatisticsAction;
import it.geosolutions.geobatch.unredd.statistics.StatisticsConfiguration;
import junit.framework.TestCase;

import org.junit.Before;

public class Test extends TestCase {

	String repositoryDir = "C:\\Users\\Luca\\Desktop\\temp";
	String workingDir = "src\\test\\resources";
	String zipFileName = "src\\test\\resources\\Request.zip";
	String xmlFileName = "Request.xml";
	public void setUp() throws Exception {


	}


	public void test()  {
	/*	StatisticsConfiguration configuration = new StatisticsConfiguration("id", "name", " description");
		configuration.setRepositoryDir(repositoryDir);
		configuration.setXmlFileName(xmlFileName);
		configuration.setWorkingDirectory(workingDir);
		File zipfile = new File(zipFileName);
		Queue<EventObject> queue = new LinkedBlockingQueue<EventObject>();
		queue.add(new FileSystemEvent(zipfile, FileSystemEventType.FILE_ADDED));
		StatisticsAction action = new StatisticsAction(configuration);
		try {
			action.execute(queue);
		} catch (ActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

}
