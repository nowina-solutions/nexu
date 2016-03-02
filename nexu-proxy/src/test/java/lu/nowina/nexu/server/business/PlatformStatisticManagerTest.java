/**
 * © Nowina Solutions, 2015-2015
 *
 * Concédée sous licence EUPL, version 1.1 ou – dès leur approbation par la Commission européenne - versions ultérieures de l’EUPL (la «Licence»).
 * Vous ne pouvez utiliser la présente œuvre que conformément à la Licence.
 * Vous pouvez obtenir une copie de la Licence à l’adresse suivante:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Sauf obligation légale ou contractuelle écrite, le logiciel distribué sous la Licence est distribué «en l’état»,
 * SANS GARANTIES OU CONDITIONS QUELLES QU’ELLES SOIENT, expresses ou implicites.
 * Consultez la Licence pour les autorisations et les restrictions linguistiques spécifiques relevant de la Licence.
 */
package lu.nowina.nexu.server.business;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;

import lu.nowina.nexu.ConfigurationException;
import lu.nowina.nexu.api.Arch;
import lu.nowina.nexu.api.JREVendor;
import lu.nowina.nexu.api.OS;
import lu.nowina.nexu.server.config.OverrideConfig;
import lu.nowina.nexu.server.config.ServiceConfig;
import lu.nowina.nexu.stats.PlatformStatistic;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * JUnit test class for {@link PlatformStatisticManager}.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class,
  classes={PlatformStatisticManagerTest.class, ServiceConfig.class, OverrideConfig.class})
@Configuration
public class PlatformStatisticManagerTest {

	private static String staticRepository;

	private static final ClockProxy CLOCK = new ClockProxy(Clock.systemUTC());

	@Autowired
	private PlatformStatisticManager manager;
	
	@Value("${platform.statistic.repository}")
	private String repository;
	
	public PlatformStatisticManagerTest() {
		super();
	}

	@Test(expected = ConfigurationException.class)
	public void testConfigurationException() {
		staticRepository = repository;
		final PlatformStatisticManager manager = new PlatformStatisticManager();
		manager.postConstruct();
	}
	
	@Test
	public void testNoException() {
		manager.addNewStatistic(null);
	}
	
	@Test
	public void testAddNewStatisticNoInfo() throws Exception {
		final Instant nextInstant = Instant.parse("1900-01-01T00:00:00.00Z");
		synchronized(CLOCK) {
			CLOCK.setNextInstant(nextInstant);
			manager.addNewStatistic(Collections.emptyMap());
		}
		final File file = getFile(1900, 01, 01, 00, 00, 00);
		Assert.assertNull(file);
	}
	
	@Test
	public void testAddNewStatisticWithoutEnvironmentInfo() throws Exception {
		final Map<String, String[]> map = new HashMap<String, String[]>();
		map.put(PlatformStatistic.APPLICATION_VERSION, new String[]{"junit.test.application.version"});
		
		final Instant nextInstant = Instant.parse("1920-01-01T00:00:00.00Z");
		synchronized(CLOCK) {
			CLOCK.setNextInstant(nextInstant);
			manager.addNewStatistic(map);
		}
		final File file = getFile(1920, 01, 01, 00, 00, 00);
		
		final JAXBContext ctx = JAXBContext.newInstance(PlatformStatistic.class);
		final PlatformStatistic stat = (PlatformStatistic) ctx.createUnmarshaller().unmarshal(file);
		Assert.assertNotNull(stat);
		Assert.assertEquals("junit.test.application.version", stat.getApplicationVersion());
		Assert.assertNull(stat.getEnvironmentInfo());
	}

	@Test
	public void testAddNewStatisticWithoutApplicationVersion() throws Exception {
		final Map<String, String[]> map = new HashMap<String, String[]>();
		map.put(PlatformStatistic.JRE_VENDOR, new String[]{JREVendor.ORACLE.toString()});

		final Instant nextInstant = Instant.parse("1940-01-01T00:00:00.00Z");
		synchronized(CLOCK) {
			CLOCK.setNextInstant(nextInstant);
			manager.addNewStatistic(map);
		}
		final File file = getFile(1940, 01, 01, 00, 00, 00);

		final JAXBContext ctx = JAXBContext.newInstance(PlatformStatistic.class);
		final PlatformStatistic stat = (PlatformStatistic) ctx.createUnmarshaller().unmarshal(file);
		Assert.assertNotNull(stat);
		Assert.assertNull(stat.getApplicationVersion());
		Assert.assertNotNull(stat.getEnvironmentInfo());
		Assert.assertNull(stat.getEnvironmentInfo().getOsArch());
		Assert.assertNull(stat.getEnvironmentInfo().getOsName());
		Assert.assertNull(stat.getEnvironmentInfo().getOsVersion());
		Assert.assertNull(stat.getEnvironmentInfo().getArch());
		Assert.assertNull(stat.getEnvironmentInfo().getOs());
		Assert.assertEquals(JREVendor.ORACLE, stat.getEnvironmentInfo().getJreVendor());
	}
	
	@Test
	public void testAddNewCompleteStatistic() throws Exception {
		final Map<String, String[]> map = new HashMap<String, String[]>();
		map.put(PlatformStatistic.APPLICATION_VERSION, new String[]{"junit.test.application.version"});
		map.put(PlatformStatistic.JRE_VENDOR, new String[]{JREVendor.ORACLE.toString()});
		map.put(PlatformStatistic.OS_ARCH, new String[]{"x86_64"});
		map.put(PlatformStatistic.OS_NAME, new String[]{"windows"});
		map.put(PlatformStatistic.OS_VERSION, new String[]{"junit.test.os.version"});
		
		final Instant nextInstant = Instant.parse("1960-01-01T00:00:00.00Z");
		synchronized(CLOCK) {
			CLOCK.setNextInstant(nextInstant);
			manager.addNewStatistic(map);
		}
		final File file = getFile(1960, 01, 01, 00, 00, 00);
		
		final JAXBContext ctx = JAXBContext.newInstance(PlatformStatistic.class);
		final PlatformStatistic stat = (PlatformStatistic) ctx.createUnmarshaller().unmarshal(file);
		Assert.assertNotNull(stat);
		Assert.assertEquals("junit.test.application.version", stat.getApplicationVersion());
		Assert.assertNotNull(stat.getEnvironmentInfo());
		Assert.assertEquals(JREVendor.ORACLE, stat.getEnvironmentInfo().getJreVendor());
		Assert.assertEquals("x86_64", stat.getEnvironmentInfo().getOsArch());
		Assert.assertEquals(Arch.AMD64, stat.getEnvironmentInfo().getArch());
		Assert.assertEquals("windows", stat.getEnvironmentInfo().getOsName());
		Assert.assertEquals(OS.WINDOWS, stat.getEnvironmentInfo().getOs());
		Assert.assertEquals("junit.test.os.version", stat.getEnvironmentInfo().getOsVersion());
	}
	
	@AfterClass
	public static void afterTests() throws IOException {
		FileUtils.deleteDirectory(new File(staticRepository));
	}
	
	@Bean
	public Clock clock() {
		return CLOCK;
	}
	
	private File getFile(int year, int month, int dayOfMonth, int hour, int minute, int second) throws URISyntaxException {
		final File targetDirectory = new File(repository,
				year + File.separator + month + File.separator + dayOfMonth + File.separator + hour + File.separator + minute + File.separator + second);
		final File[] files = targetDirectory.listFiles();
		if((files == null) || (files.length == 0)) {
			return null;
		}
		if(files.length > 1) {
			throw new IllegalArgumentException("More than one file in " + targetDirectory.getAbsolutePath());
		}
		return files[0];
	}
	
	private static class ClockProxy extends Clock {

		private Clock proxied;
		
		private Instant nextInstant;
		
		public ClockProxy(final Clock proxied) {
			this.proxied = proxied;
		}
		
		@Override
		public ZoneId getZone() {
			return proxied.getZone();
		}

		@Override
		public Clock withZone(ZoneId zone) {
			return proxied.withZone(zone);
		}

		@Override
		public Instant instant() {
			return nextInstant;
		}
		
		public void setNextInstant(final Instant nextInstant) {
			this.nextInstant = nextInstant;
		}
	}
}
