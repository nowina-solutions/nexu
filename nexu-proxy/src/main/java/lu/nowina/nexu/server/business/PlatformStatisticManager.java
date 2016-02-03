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
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import lu.nowina.nexu.ConfigurationException;
import lu.nowina.nexu.TechnicalException;
import lu.nowina.nexu.api.Arch;
import lu.nowina.nexu.api.EnvironmentInfo;
import lu.nowina.nexu.api.JREVendor;
import lu.nowina.nexu.api.OS;
import lu.nowina.nexu.stats.PlatformStatistic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service used to manage {@link PlatformStatistic}.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
@Service
public class PlatformStatisticManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(PlatformStatisticManager.class.getName());

	@Value("${platform.statistic.repository}")
	private String repository;

	private final JAXBContext ctx;

	private File repositoryDir;

	@Autowired(required = false)
	private Clock clock;
	
	public PlatformStatisticManager() {
		try {
			ctx = JAXBContext.newInstance(PlatformStatistic.class);
		} catch (final JAXBException e) {
			LOGGER.error("Cannot instantiate JAXBContext", e);
			throw new TechnicalException("Cannot instantiate JAXBContext for PlatformStatistic", e);
		}
	}

	@PostConstruct
	public void postConstruct() {
		if(clock == null) {
			clock = Clock.systemDefaultZone();
		}
		
		if (repository == null) {
			throw new ConfigurationException("Configuration must defined 'platform.statistic.repository'");
		}

		repositoryDir = new File(repository);
		if (!repositoryDir.exists() && !repositoryDir.mkdirs()) {
			throw new ConfigurationException("Cannot create repository " + repositoryDir.getAbsolutePath());
		}

		if (!repositoryDir.isDirectory() || !repositoryDir.canWrite()) {
			throw new ConfigurationException(repositoryDir.getAbsolutePath() + " cannot be used for repository");
		}
	}
	
	/**
	 * Adds a new {@link PlatformStatistic} using parameter map from {@link HttpServletRequest}.
	 * <p>The method does not throw any exception because of the optional nature of this operation.
	 * @param servletParameterMap The map containing the data for the statistic.
	 */
	public void addNewStatistic(final Map<String, String[]> servletParameterMap) {
		try {
			final PlatformStatistic platformStatistic = buildPlatformStatistic(servletParameterMap);
			if(platformStatistic != null) {
				savePlatformStatistic(platformStatistic);
			}
		} catch(final Exception e) {
			LOGGER.error("An exception occurred when trying to add a new statistic.", e);
		}
	}
	
	private PlatformStatistic buildPlatformStatistic(Map<String, String[]> servletParameterMap) {
		final PlatformStatistic platformStatistic = new PlatformStatistic();
		final EnvironmentInfo environmentInfo = new EnvironmentInfo();
		boolean environmentInfoStatistic = false;
		for(final Entry<String, String[]> entry : servletParameterMap.entrySet()) {
			switch(entry.getKey()) {
			case PlatformStatistic.APPLICATION_VERSION:
				platformStatistic.setApplicationVersion(entry.getValue()[0]);
				break;
			case PlatformStatistic.JRE_VENDOR:
				try {
					environmentInfo.setJreVendor(JREVendor.valueOf(entry.getValue()[0]));
				} catch (final IllegalArgumentException e) {
					LOGGER.warn("Unknown JREVendor value: " + entry.getValue()[0]);
					environmentInfo.setJreVendor(JREVendor.NOT_RECOGNIZED);
				}
				environmentInfoStatistic = true;
				break;
			case PlatformStatistic.OS_ARCH:
				environmentInfo.setOsArch(entry.getValue()[0]);
				environmentInfo.setArch(Arch.forOSArch(entry.getValue()[0]));
				environmentInfoStatistic = true;
				break;
			case PlatformStatistic.OS_NAME:
				environmentInfo.setOsName(entry.getValue()[0]);
				environmentInfo.setOs(OS.forOSName(entry.getValue()[0]));
				environmentInfoStatistic = true;
				break;
			case PlatformStatistic.OS_VERSION:
				environmentInfo.setOsVersion(entry.getValue()[0]);
				environmentInfoStatistic = true;
				break;
			}
		}
		if(environmentInfoStatistic) {
			platformStatistic.setEnvironmentInfo(environmentInfo);
		}
		return ((platformStatistic.getApplicationVersion() != null) || environmentInfoStatistic) ? platformStatistic : null;
	}
	
	private void savePlatformStatistic(final PlatformStatistic platformStatistic) throws IOException, JAXBException {
		final File file = File.createTempFile("platform-statistic", ".xml", getTargetDirectory());
		ctx.createMarshaller().marshal(platformStatistic, file);
	}
	
	private File getTargetDirectory() {
		final LocalDateTime now = LocalDateTime.now(clock);
		final int year = now.getYear();
		final int month = now.getMonth().getValue();
		final int dayOfMonth = now.getDayOfMonth();
		final int hour = now.getHour();
		final int minute = now.getMinute();
		final int second = now.getSecond();
		final File targetDirectory = new File(repositoryDir,
				year + File.separator + month + File.separator + dayOfMonth + File.separator + hour + File.separator + minute + File.separator + second);
		if(!targetDirectory.exists() && !targetDirectory.mkdirs()) {
			throw new IllegalStateException("Cannot create target directory " + targetDirectory.getAbsolutePath());
		}
		if(!targetDirectory.isDirectory() || !targetDirectory.canWrite()) {
			throw new IllegalStateException("Target directory " + targetDirectory.getAbsolutePath() + " is not a directory or is not writeable.");
		}
		return targetDirectory;
	}
}
