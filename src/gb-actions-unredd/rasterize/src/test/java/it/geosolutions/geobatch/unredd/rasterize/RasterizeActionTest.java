package it.geosolutions.geobatch.unredd.rasterize;

import it.geosolutions.filesystemmonitor.monitor.FileSystemEvent;
import it.geosolutions.filesystemmonitor.monitor.FileSystemEventType;
import it.geosolutions.geobatch.flow.event.action.ActionException;

import java.io.File;
import java.util.EventObject;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;

public class RasterizeActionTest {

	@Test
	public void test() throws ActionException {
		RasterizeConfiguration rc = new RasterizeConfiguration("id","name","description");
		rc.setLayername("states");
		rc.setRasterAttribName("FAMILIES");
		rc.setRasterpixelheight(3876);
		rc.setRasterpixelwidth(3562);
		rc.setRasterx0(-68);
		rc.setRasterx1(-124);
		rc.setRastery0(28);
		rc.setRastery1(51);
		rc.setWorkingDirectory("src/test/resources");

		Queue<EventObject> queue = new LinkedBlockingQueue<EventObject>();
		File inputFile = new File("src/test/resources/cell_pol_roi2.shp");
		queue.add(new FileSystemEvent(inputFile, FileSystemEventType.FILE_ADDED));

		RasterizeAction action = new RasterizeAction(rc);
		action.execute(queue);
	}

}
