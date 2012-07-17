package it.geosolutions.geobatch.unredd.rasterize;

import it.geosolutions.filesystemmonitor.monitor.FileSystemEvent;
import it.geosolutions.filesystemmonitor.monitor.FileSystemEventType;
import it.geosolutions.geobatch.flow.event.action.ActionException;

import java.io.File;
import java.util.EventObject;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import junit.framework.Assert;
import org.apache.commons.io.FileUtils;


import org.junit.Before;
import org.junit.Test;

public class GDALRasterizeTest extends BaseTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws ActionException {
		File inputFile = loadFile("cell_pol_roi2.shp");
        Assert.assertNotNull(inputFile);

		GDALRasterizeConfiguration rc = new GDALRasterizeConfiguration("id","name","description");
		rc.setLayername("states");
		rc.setRasterAttribName("FAMILIES");
		rc.setRasterpixelheight(3876);
		rc.setRasterpixelwidth(3562);
		rc.setRasterx0(-68);
		rc.setRasterx1(-124);
		rc.setRastery0(28);
		rc.setRastery1(51);
		rc.setConfigDir(inputFile.getParentFile());
        rc.setFilename(null);

		GDALRasterizeAction action = new GDALRasterizeAction(rc);
        action.setTempDir(getTempDir());

		Queue<EventObject> queue = new LinkedBlockingQueue<EventObject>();
		queue.add(new FileSystemEvent(inputFile, FileSystemEventType.FILE_ADDED));
		action.execute(queue);
	}

    @Test
    public void testFromShape() throws Exception {
        FileUtils.forceMkdir(workingDirFile);

        GDALRasterize rasterize = new GDALRasterize("rasterize/commongdalrasterizeshp.xml", /* the template */
                gdalpath, /* the command to execute*/
                "rasterize/commongdalrasterize.xsl", /* the transformation file */
                workingDirFile, workingDirFile.getAbsolutePath());
        File errorFile = File.createTempFile("error", ".xml", workingDirFile);
        options.setSrc("src/test/resources/layer1_2012.shp");
        File file = rasterize.run(options,"pipposhp1.tif", errorFile);
        assertTrue(file.exists());
        file.delete();

    }

}
