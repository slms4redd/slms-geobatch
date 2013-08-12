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
package it.geosolutions.geobatch.unredd.statistics;

import it.geosolutions.geobatch.flow.event.action.ActionException;
import it.geosolutions.geobatch.flow.event.action.BaseAction;

import java.util.EventObject;
import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Luca Paolino - luca.paolino@geo-solutions.it
 * 
 */
public class StatisticsAction extends BaseAction<EventObject> {
    private final static Logger LOGGER = LoggerFactory.getLogger(StatisticsAction.class);

    /**
     * configuration
     */
    private final StatisticsConfiguration conf;

    public StatisticsAction(StatisticsConfiguration configuration) {
        super(configuration);
        conf = configuration;
        //TODO initialize your members here
    }

    /**
     * Removes TemplateModelEvents from the queue and put
     */
    public Queue<EventObject> execute(Queue<EventObject> events) throws ActionException {
    	
        // return
        final Queue<EventObject> ret=new LinkedList<EventObject>();
      //  GeotiffRetilerConfiguration retilerConfig=new GeotiffRetilerConfiguration(configuration.getId(),"BRISEIDE_retiler",configuration.getDescription());

        while (events.size() > 0) {
            final EventObject ev;
            try {
                if ((ev = events.remove()) != null) {
                    if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace("StatisticsAction.execute(): working on incoming event: "+ev.getSource());
                    }

//                    File dirToDelete = new File (uncompressedFileDir);
//                    FileManager.deleteDir(dirToDelete);
                    // add the event to the return
					ret.add(ev);
					
                } else {
                    if (LOGGER.isErrorEnabled()) {
                        LOGGER.error("StatisticsAction.execute(): Encountered a NULL event: SKIPPING...");
                    }
                    continue;
                }
            } catch (Exception ioe) {
            ioe.printStackTrace();
                final String message = "StatisticsAction.execute(): Unable to produce the output: "
                        + ioe.getLocalizedMessage();
                if (LOGGER.isErrorEnabled())
                    LOGGER.error(message);
                throw new ActionException(this, message);
            }
        }
        
        return ret;
    }

    @Override
    public boolean checkConfiguration() {
        return true;
    }
    
}
