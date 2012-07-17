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

import it.geosolutions.filesystemmonitor.monitor.FileSystemEvent;
import it.geosolutions.filesystemmonitor.monitor.FileSystemEventType;
import it.geosolutions.geobatch.flow.event.action.ActionException;
import it.geosolutions.geobatch.flow.event.action.BaseAction;


import it.geosolutions.imageio.plugins.tiff.TIFFImageWriteParam;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.Queue;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.imageio.GeoToolsWriteParams;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.filter.Filter;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffWriteParams;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.process.raster.VectorToRasterProcess;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.ProgressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author Luca Paolino - luca.paolino@geo-solutions.it
 * 
 */
public class GDALRasterizeAction extends BaseAction<EventObject> {

    private final static Logger LOGGER = LoggerFactory.getLogger(GDALRasterizeAction.class);

	private JAXBContext context = null;
	private Unmarshaller unmarshaller = null;

	/**
	 * configuration
	 */
	private final GDALRasterizeConfiguration conf;
	private SimpleFeatureCollection features = null;
	
	
	public GDALRasterizeAction(GDALRasterizeConfiguration configuration) {
		super(configuration);
		conf = configuration;
		// TODO initialize your members here

	}
	
	

	public SimpleFeatureCollection getFeatures() {
        return features;
    }



    public void setFeatures(SimpleFeatureCollection features) {
        this.features = features;
    }



    /**
	 * Removes TemplateModelEvents from the queue and put
	 */
	public Queue<EventObject> execute(Queue<EventObject> events)
			throws ActionException {
		final Queue<EventObject> ret = new LinkedList<EventObject>();

		
		
		while ( ! events.isEmpty() ) {
			final EventObject ev;

			try {
				if ((ev = events.remove()) != null) {
					if (LOGGER.isTraceEnabled()) {
						LOGGER.trace("RasterizeAction.execute(): working on incoming event: "
								+ ev.getSource());
					}
					String rasterAttribName = conf.getRasterAttribName();
					String rasterCqlFilter =  "";
					if (conf.getRasterCqlFilter()!=null) rasterCqlFilter =  conf.getRasterCqlFilter();
					
					double noData = conf.getRasterNoData();
			
//					System.out.println("NODATA"+noData);
					int rasterpixelheight = 	conf.getRasterpixelheight();
					int rasterpixelwidth = 	conf.getRasterpixelwidth();
					double rasterx0 = conf.getRasterx0();
					double rasterx1 = conf.getRasterx1();
					double rastery0 = conf.getRastery0();
					double rastery1 = conf.getRastery1();
				
                    int height = conf.getRetileH();
                    int width = conf.getRetileW();

					String layername = conf.getLayername();
					
					if (layername==null) throw new ActionException(this, "the layer name cannot be empty");
					if (rasterpixelheight==0.0d || rasterpixelwidth==0.0d) throw new ActionException(this, "the raster pixel size is wrong");
					
					FileSystemEvent fileEvent=(FileSystemEvent)ev;
					File file = fileEvent.getSource();
					FileDataStore store = null;
					// this action can be used also getting a simpleFeatureCollection from another resource than a file
					// in this case the features field is not null but it is filled externally
                    if (features==null) {
                        
    					store = FileDataStoreFinder.getDataStore(file);
    					if (store==null) throw new ActionException(this, "the layer "+file.getCanonicalPath()+" cannot be found. Skip execution.");
                        FeatureSource featureSource = store.getFeatureSource();
                        SimpleFeatureType schema = (SimpleFeatureType) featureSource.getSchema();
                        Query query = new Query( schema.getTypeName(), Filter.INCLUDE );
                        query.setCoordinateSystem(CRS.decode("EPSG:4326", true));
                        features = (SimpleFeatureCollection) featureSource.getFeatures( query );
                        
    			//		features = (SimpleFeatureCollection) featureSource.getFeatures();
                    }
                    
					//CoordinateReferenceSystem crs = DefaultGeographicCRS.WGS84;
					CoordinateReferenceSystem crs = features.getSchema().getCoordinateReferenceSystem();
					ReferencedEnvelope bounds = new ReferencedEnvelope(rasterx0, rasterx1, rastery0, rastery1,crs);

			        Dimension gridDim = new Dimension(rasterpixelwidth, rasterpixelheight);

			        String covName = layername;
			        ProgressListener monitor = null;

			        GridCoverage2D cov = VectorToRasterProcess.process(features, rasterAttribName, gridDim, bounds, covName, monitor);
			        LOGGER.debug("CRS :"+cov.getCoordinateReferenceSystem().toWKT());
//			        System.out.println(cov.toString());

//					Map<String, Object> attribute = new HashMap<String, Object>();
//					attribute.put("rasterAttribName", rasterAttribName);
//					if (rasterCqlFilter!=null) attribute.put("rasterCqlFilter", rasterCqlFilter);
					File tmpTiff = null;
					if (conf.getFilename()!=null) 
					    tmpTiff = new File(conf.getWorkingDirectory(),conf.getFilename());
					else  
					    tmpTiff =   new File(conf.getWorkingDirectory(),"testcov.tif");
					GeoTiffWriter writer = null;
					try {
                        writer = new GeoTiffWriter(tmpTiff);
                        
						if (height>0 && width>0) {
							GeoTiffWriteParams params = new GeoTiffWriteParams();
							
                            params.setTilingMode(TIFFImageWriteParam.MODE_EXPLICIT);
                            params.setTiling(conf.getRetileH(), conf.getRetileW());
                            ParameterValue<GeoToolsWriteParams> value = GeoTiffFormat.GEOTOOLS_WRITE_PARAMS.createValue();
                            value.setValue(params);
                            writer.write(cov, new GeneralParameterValue[]{value});
                        }
                        else 
                        	writer.write(cov, null );
					} catch (IOException e) {
						// TODO Auto-generated catch block
						LOGGER.error("An exception was raised executing the RasterizeAction",e);
						throw new ActionException(this,"An exception was raised executing the RasterizeAction");
                    }
                    finally {
                    //	if (writer!=null) writer.dispose();
    			        FileSystemEvent fileSystemInsertEvent = new FileSystemEvent(tmpTiff, FileSystemEventType.FILE_ADDED);
    					ret.add(fileSystemInsertEvent);
    					if (store!=null) store.dispose();
                    }
					
					// add the event to the return


				} else {
					if (LOGGER.isErrorEnabled()) {
						LOGGER.error("RasterizeAction.execute(): Encountered a NULL event: SKIPPING...");
					}
					continue;
				}
			} catch (Throwable ioe) {
				ioe.printStackTrace();
				final String message = "RasterizeAction.execute(): Unable to produce the output: "
						+ ioe.getLocalizedMessage();
				// LOGGER.error(message, ioe);
				if (LOGGER.isErrorEnabled())
					LOGGER.error(message);
				throw new ActionException(this, message);
			}
		}

		return ret;
	}

}
