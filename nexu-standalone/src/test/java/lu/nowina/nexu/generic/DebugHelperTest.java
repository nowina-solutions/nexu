package lu.nowina.nexu.generic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lu.nowina.nexu.NexuException;
import lu.nowina.nexu.api.AppConfig;
import lu.nowina.nexu.api.EnvironmentInfo;
import lu.nowina.nexu.api.Feedback;

public class DebugHelperTest {

	private DebugHelper dh;
	private File dummyNexuHome;

	@Before
	public void setup() {
		Properties properties = new Properties();
		properties.setProperty("os.version", "27");
		properties.setProperty("os.name", "Fedora Linux");
		properties.setProperty("java.vendor", "Oracle");
		properties.setProperty("os.arch", "x86_64");

		AppConfig appConfig = spy(new AppConfig());
		dummyNexuHome = Paths.get("tmp", "dummy_nexu_home").toFile();
		assertTrue(dummyNexuHome.mkdirs());
		assertTrue(dummyNexuHome.isDirectory());
		when(appConfig.getNexuHome()).thenReturn(dummyNexuHome);
		dh = spy(DebugHelper.class);
		when(dh.getConfig()).thenReturn(appConfig);
		when(dh.getProperties()).thenReturn(properties);
	}

	@After
	public void cleanup() throws IOException {
		if (dummyNexuHome.exists()) {
			FileUtils.forceDelete(Paths.get("tmp").toFile());
		}
	}

	@Test
	public void testCollectDebugData() throws JAXBException {

		Feedback feedback = dh.collectDebugData(new NexuException("Dummy exception"));
		assertEquals("LINUX", feedback.getInfo().getOs().name());
		assertEquals(NexuException.class, feedback.getException().getClass());
	}

	@Test
	public void testBuildDebugFileWhenLogFound() throws IOException {
		Path nexuLogPath = Paths.get(dummyNexuHome.getAbsolutePath(), "nexu.log");
		File nexuLog = nexuLogPath.toFile();
		nexuLog.createNewFile();
		assertTrue(nexuLog.exists());
		String dummyLogContent = "Dummy log content";
		Files.write(nexuLogPath, dummyLogContent.getBytes());
		dh.buildDebugFile();
		Path nexuDebugPath = Paths.get(dummyNexuHome.getAbsolutePath(), dh.getDebugFileName());
		assertTrue(nexuDebugPath.toFile().exists());
	}

	@Test
	public void testBuildDebugFileWhenLogNotFound() throws IOException {
		dh.buildDebugFile();
		Path nexuDebugPath = Paths.get(dummyNexuHome.getAbsolutePath(), dh.getDebugFileName());
		assertTrue(nexuDebugPath.toFile().exists());
	}

	@Test
	public void testAppendFeedbackData() throws JAXBException, IOException {
		Path nexuDebugPath = Paths.get(dummyNexuHome.getAbsolutePath(), dh.getDebugFileName());
		Feedback feedback = new Feedback();
		feedback.setNexuVersion("99");
		FileUtils.write(nexuDebugPath.toFile(),
				String.format("dummy log content %s %s", System.lineSeparator(), System.lineSeparator()), "utf-8");
		File readyFile = dh.appendFeedbackData(nexuDebugPath, feedback);
		File expectedFile = new File(
				this.getClass().getClassLoader().getResource("lu/nowina/nexu/generic/DebugHelperTest.txt").getPath());
		assertTrue(FileUtils.contentEqualsIgnoreEOL(expectedFile, readyFile, "utf-8"));
	}

	@Test
	public void testShowDebugFileInExplorer() {
		Feedback feedback = new Feedback();
		feedback.setInfo(EnvironmentInfo.buildFromSystemProperties(System.getProperties()));
		dh.showDebugFileInExplorer(Paths.get(
				this.getClass().getClassLoader().getResource("lu/nowina/nexu/generic/DebugHelperTest.txt").getPath()),
				feedback);
	}

}
