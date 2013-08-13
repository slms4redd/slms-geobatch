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

import it.geosolutions.unredd.stats.impl.StatsRunner;
import it.geosolutions.unredd.stats.model.config.StatisticConfiguration;
import it.geosolutions.unredd.stats.model.config.util.TokenResolver;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Statistics {
	
    private final static Logger LOGGER = LoggerFactory.getLogger(Statistics.class);

    public static enum Tokens {
        FILEPATH,
        LAYERNAME,
        YEAR,
        MONTH
    }
    
	 private JAXBContext context= null;
	 private Unmarshaller unmarshaller = null; 
	
	
	public Statistics() throws JAXBException {
		super();
		context = JAXBContext.newInstance(StatisticConfiguration.class);
		unmarshaller = context.createUnmarshaller();

	}


	public void executeStatistics(String cfgdata, Map<Tokens, String> layerUpdateProperties, String destFileName) throws JAXBException, IOException {
        LOGGER.debug("Statistics input properties"+layerUpdateProperties.toString());		
		StatisticConfiguration cfg = (StatisticConfiguration) unmarshaller.unmarshal(new StringReader(cfgdata));

		TokenResolver<StatisticConfiguration, Tokens> resolver =
                new TokenResolver(cfg, StatisticConfiguration.class);

		resolver.putAll(layerUpdateProperties);
		
		cfg = (StatisticConfiguration)resolver.resolve();

		// initialize the file when saving statistics
		
		cfg.getOutput().setFile(destFileName);
		StatsRunner runner = new StatsRunner(cfg);
		runner.run();
	}
}
