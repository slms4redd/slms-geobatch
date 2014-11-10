package it.geosolutions.geobatch.imagemosaic;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;
import it.geosolutions.geoserver.rest.encoder.coverage.GSCoverageEncoder;
import it.geosolutions.geoserver.rest.encoder.coverage.GSImageMosaicEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder.Presentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;

public class ImageMosaicTest /*extends BaseTest*/ {

	private static final String GS_URL = "http://178.33.8.115/stg_geoserver";
	private static final String GS_UID = "admin";
	private static final String GS_PSWD = "1salvaDoR-";
	private static final String WORKSPACE = "rdc";
	private static final String STORE_NAME = "deforestation";
	private static final String MOSAIC_DIR_PATH = "//var//stg_geoserver//extdata//deforestation//";
	
	
	//@Ignore
	//public void createMosaicLayerTest() throws Exception{
	public static void main(String [] args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, FileNotFoundException, URISyntaxException{
//		ImageMosaicConfiguration imc = new ImageMosaicConfiguration("testConfig", "testConfig", "testConfig");
//		ImageMosaicAction ima = new ImageMosaicAction(imc);
//
//		//Run the internal createMosaicLayer method... It's private so reflection is needed
//		Method met = ima.getClass().getDeclaredMethod("createMosaicLayer", ImageMosaicCommand.class , File.class, String.class, ImageMosaicGranulesDescriptor.class, String.class, GeoServerRESTPublisher.class, String.class);
//		met.setAccessible(true);
//		met.invoke(ima, null, null, null, null, null, null, null);
		
		File mosaicDir = new File(MOSAIC_DIR_PATH);
		
		GSCoverageEncoder coverageEnc = buildTestGSCoverageEncoder();
		GSLayerEncoder layerEnc = buildTestGSLayerEncoder();
		
		
		final GeoServerRESTPublisher gsPublisher = new GeoServerRESTPublisher(GS_URL,GS_UID,GS_PSWD);
		final boolean published = gsPublisher.publishExternalMosaic(WORKSPACE, STORE_NAME, mosaicDir,coverageEnc, layerEnc);
		Assert.assertTrue("yeah", published);
	}
	
	private static  GSCoverageEncoder buildTestGSCoverageEncoder(){
	
		GSImageMosaicEncoder coverageEnc = new GSImageMosaicEncoder();
		coverageEnc.setEnabled(true);
		
		GSDimensionInfoEncoder gsdie = new GSDimensionInfoEncoder();
		gsdie.setEnabled(true);
		gsdie.setPresentation(Presentation.LIST);
		coverageEnc.setMetadata(null, gsdie);
		
		coverageEnc.setMaxAllowedTiles(2147483647);
		coverageEnc.setBackgroundValues("-1.0");
		coverageEnc.setInputTransparentColor("");
		coverageEnc.setAllowMultithreading(false);
		coverageEnc.setUSE_JAI_IMAGEREAD(false);
		coverageEnc.setSUGGESTED_TILE_SIZE("256,256");
		
		
		coverageEnc.setTitle("deforestation");
		coverageEnc.setSRS("EPSG:4326");
		coverageEnc.setProjectionPolicy(ProjectionPolicy.NONE);
		coverageEnc.setLatLonBoundingBox(11.10107, -14.02949, 32.23628, 5.39593, "EPSG:4326");
		coverageEnc.setName("deforestation");
		
		return coverageEnc;
	}

	private static GSLayerEncoder buildTestGSLayerEncoder(){
	
		GSLayerEncoder layerEnc = new GSLayerEncoder();
		layerEnc.setEnabled(true);
		layerEnc.setDefaultStyle("raster");
		
		return layerEnc;
	}
	
}
