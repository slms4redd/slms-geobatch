/*
 *  Copyright (C) 2007 - 2011 GeoSolutions S.A.S.
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

import it.geosolutions.filesystemmonitor.monitor.FileSystemEvent;
import it.geosolutions.filesystemmonitor.monitor.FileSystemEvent;
import it.geosolutions.filesystemmonitor.monitor.FileSystemEventType;
import it.geosolutions.geobatch.flow.event.action.Action;
import it.geosolutions.geobatch.flow.event.action.ActionException;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class SingleFileActionExecutor {
    private final static Logger LOGGER = LoggerFactory.getLogger(SingleFileActionExecutor.class);

    public static File execute(Action a, File file) throws ActionException {

		Queue<FileSystemEvent> inQueue = new LinkedList<FileSystemEvent>();
        if(file != null)
            inQueue.add(new FileSystemEvent(file, FileSystemEventType.FILE_ADDED));

        Queue<FileSystemEvent> outQueue = a.execute(inQueue);
        switch(outQueue.size()) {
            case 0:
                return null;
            case 1:
                return outQueue.poll().getSource();
            default:
                LOGGER.error("Single output file expected: found more than one:");
                for (FileSystemEvent ev : outQueue) {
                    LOGGER.error(" event: " + ev);
                }
                throw new ActionException(a, "Got more than one output file: " + outQueue);
        }
    }

    public static Collection<File> executeMultiReturn(Action a, File file) throws ActionException {

		Queue<FileSystemEvent> inQueue = new LinkedList<FileSystemEvent>();
        if(file != null)
            inQueue.add(new FileSystemEvent(file, FileSystemEventType.FILE_ADDED));

        List<File> ret = new ArrayList<File>();

        Queue<FileSystemEvent> outQueue = a.execute(inQueue);
        for (FileSystemEvent ev : outQueue) {
            ret.add(ev.getSource());
        }
        return ret;
    }

}
