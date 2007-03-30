/**
 * 
 */
package cytoscape.plugin;

import cytoscape.plugin.PluginTracker.PluginStatus;

import java.io.*;
import org.jdom.*;
import org.jdom.input.*;
import java.util.*;
import junit.framework.TestCase;

/**
 * @author skillcoy
 *
 */
public class PluginTrackerTest extends TestCase {
	private	SAXBuilder builder;

	private Document xmlDoc;
	private PluginTracker tracker;
	private String fileName = "test_tracker.xml";
	private File tmpDir;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		builder = new SAXBuilder(false);
		tmpDir = new File(System.getProperty("java.io.tmpdir"));
		tracker = new PluginTracker(tmpDir, fileName);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		tracker.delete();
	}

	/**
	 * Test method for {@link cytoscape.plugin.PluginTracker#PluginTracker()}.
	 */
	public void testPluginTracker() throws Exception {
		Document Doc = getDoc();
		assertNotNull(Doc);
		
		assertEquals(Doc.getRootElement().getName(), "CytoscapePlugin");
		assertEquals(Doc.getRootElement().getChildren().size(), 3);
		
		assertNotNull(Doc.getRootElement().getChild("CurrentPlugins"));
		assertNotNull(Doc.getRootElement().getChild("InstallPlugins"));
		assertNotNull(Doc.getRootElement().getChild("DeletePlugins"));
		
		assertEquals(Doc.getRootElement().getChild("CurrentPlugins").getChildren().size(), 0);
		assertEquals(Doc.getRootElement().getChild("InstallPlugins").getChildren().size(), 0);
		assertEquals(Doc.getRootElement().getChild("DeletePlugins").getChildren().size(), 0);
	}

	/**
	 * Test method for {@link cytoscape.plugin.PluginTracker#getListByStatus(cytoscape.plugin.PluginTracker.PluginStatus)}.
	 */
	public void testGetListByStatus() throws Exception {
		tracker.addPlugin(getInfoObj(), PluginStatus.CURRENT);
		
		assertNotNull(tracker.getListByStatus(PluginStatus.CURRENT));
		assertEquals(tracker.getListByStatus(PluginStatus.CURRENT).size(), 1);

		// lets just check with the xml doc itself to be sure
		Document Doc = getDoc();
		Element Current = Doc.getRootElement().getChild("CurrentPlugins");
		assertEquals(Current.getChildren().size(), 1);
		assertEquals(Doc.getRootElement().getChild("InstallPlugins").getChildren().size(), 0);
		assertEquals(Doc.getRootElement().getChild("DeletePlugins").getChildren().size(), 0);
	}

	/**
	 * Test method for {@link cytoscape.plugin.PluginTracker#addPlugin(cytoscape.plugin.PluginInfo, cytoscape.plugin.PluginTracker.PluginStatus)}.
	 */
	public void testAddPlugin() throws Exception {
		tracker.addPlugin(getInfoObj(), PluginStatus.CURRENT);
		assertEquals(tracker.getListByStatus(PluginStatus.CURRENT).size(), 1);
		
		PluginInfo obj = getInfoObj();
		obj.setName("myInstallTest");
		obj.setProjectUrl("http://booya.com/foo.xml");
		tracker.addPlugin(obj, PluginStatus.INSTALL);
		
		assertEquals(tracker.getListByStatus(PluginStatus.INSTALL).size(), 1);
		
		obj.setName("mySecondInstallTest");
		tracker.addPlugin(obj, PluginStatus.INSTALL);
		
		assertEquals(tracker.getListByStatus(PluginStatus.INSTALL).size(), 2);
		
		// lets just check with the xml doc itself to be sure
		Document Doc = getDoc();
		Element Install = Doc.getRootElement().getChild("InstallPlugins");
		assertEquals(Install.getChildren().size(), 2);
		assertEquals(Doc.getRootElement().getChild("CurrentPlugins").getChildren().size(), 1);
		assertEquals(Doc.getRootElement().getChild("DeletePlugins").getChildren().size(), 0);
	}

	/**
	 * Test method for {@link cytoscape.plugin.PluginTracker#removePlugin(cytoscape.plugin.PluginInfo, cytoscape.plugin.PluginTracker.PluginStatus)}.
	 */
	public void testRemovePlugin() throws Exception {
		tracker.addPlugin(getInfoObj(), PluginStatus.CURRENT);
		assertEquals(tracker.getListByStatus(PluginStatus.CURRENT).size(), 1);
		
		PluginInfo obj = getInfoObj();
		obj.setName("myInstallTest");
		obj.setProjectUrl("http://foobar.org/y.xml");
		tracker.addPlugin(obj, PluginStatus.INSTALL);
		
		assertEquals(tracker.getListByStatus(PluginStatus.INSTALL).size(), 1);

		// won't change because this object wasn't an install object
		tracker.removePlugin(getInfoObj(), PluginStatus.INSTALL);
		assertEquals(tracker.getListByStatus(PluginStatus.INSTALL).size(), 1);

		tracker.removePlugin(obj, PluginStatus.INSTALL);
		assertEquals(tracker.getListByStatus(PluginStatus.INSTALL).size(), 0);
		
		Document Doc = getDoc();
		assertEquals(Doc.getRootElement().getChild("CurrentPlugins").getChildren().size(), 1);
		assertEquals(Doc.getRootElement().getChild("InstallPlugins").getChildren().size(), 0);
		assertEquals(Doc.getRootElement().getChild("DeletePlugins").getChildren().size(), 0);
	}


	private Document getDoc() throws Exception {
		File TestFile = new File( tmpDir, fileName);
		assertTrue(TestFile.exists());
		assertTrue(TestFile.canRead());
		// lets just check with the xml doc itself to be sure
		Document Doc = builder.build(TestFile);
		assertNotNull(Doc);
		return Doc;
	}
	
	private PluginInfo getInfoObj() {
		PluginInfo infoObj = new PluginInfo("123");
		infoObj.setName("myTest");
		infoObj.setCategory("Test");
		infoObj.setCytoscapeVersion("2.5");
		infoObj.setPluginClassName("0.2");
		infoObj.setProjectUrl("http://test.com/x.xml");
		infoObj.setFiletype(PluginInfo.FileType.JAR);
		return infoObj;
	}
	
}
