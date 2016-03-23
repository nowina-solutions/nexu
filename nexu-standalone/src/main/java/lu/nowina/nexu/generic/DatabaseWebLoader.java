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
package lu.nowina.nexu.generic;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lu.nowina.nexu.NexuLauncher;
import lu.nowina.nexu.api.AppConfig;

public class DatabaseWebLoader implements SCDatabaseRefresher {

	private static final int TIMER_RATE = 1;
	private static final int UPDATE_RATE = 10;
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseWebLoader.class.getName());

	private byte[] databaseData;
	private SCDatabase database;

	private final String serverUrl;

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			final Thread t = Executors.defaultThreadFactory().newThread(r);
			t.setDaemon(true);
			return t;
		}
	});

	private ScheduledFuture<?> timerHandle;

	private ScheduledFuture<?> updateHandle;

	private final HttpDataLoader dataLoader;

	private final Runnable timerTask = new Runnable() {
		public void run() {
			if (updateHandle != null) {
				LOGGER.info("Update still running");
			} else {
				LOGGER.info("Schedule update");
				scheduleUpdate();
			}
		}
	};

	private final Runnable updateTask = new Runnable() {
		public void run() {
			LOGGER.info("Attempt update");
			try {
				updateDatabase();
				updateHandle.cancel(false);
				updateHandle = null;
				scheduleTimer();
			} catch (IOException | JAXBException e) {
				LOGGER.error("Cannot update database", e);
			}
		}
	};

	public DatabaseWebLoader(AppConfig config, HttpDataLoader dataLoader) throws IOException {
		this.serverUrl = config.getServerUrl();
		this.dataLoader = dataLoader;

		try {
			loadDatabaseFromCache();
		} catch (IOException | JAXBException e) {
			LOGGER.error("Cannot load database from cache", e);
		}

		try {
			updateDatabase();
		} catch (IOException | JAXBException e) {
			LOGGER.error("Cannot update database", e);
		}

	}

	private void loadDatabaseFromCache() throws IOException, JAXBException {
		final File databaseWeb = getDatabaseFile();
		if (databaseWeb.exists() && databaseWeb.length() > 0) {
			try (FileInputStream in = new FileInputStream(databaseWeb)) {
				databaseData = IOUtils.toByteArray(in);
				parseDatabase();
			}
		}
	}

	public File getDatabaseFile() {
		final File nexuHome = NexuLauncher.getNexuHome();
		return new File(nexuHome, "database-web.xml");
	}

	private SCDatabase parseDatabase() throws JAXBException {
		final JAXBContext ctx = JAXBContext.newInstance(SCDatabase.class);
		final Unmarshaller u = ctx.createUnmarshaller();
		return (SCDatabase) u.unmarshal(new ByteArrayInputStream(databaseData));
	}

	public String digestDatabase() {
		if (databaseData == null) {
			return null;
		} else {
			try {
				final MessageDigest md = MessageDigest.getInstance("MD5");
				return new String(Hex.encodeHex(md.digest(databaseData)));
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void updateDatabase() throws JAXBException, IOException {
		final NexuInfo info = fetchNexuInfo();
		if (info != null && !info.getDatabaseVersion().equals(digestDatabase())) {
			fetchDatabase();
		}
	}

	private void fetchDatabase() throws IOException {
		databaseData = dataLoader.fetchDatabase(serverUrl + "/database.xml");
		if(databaseData != null) {
			try (FileOutputStream out = new FileOutputStream(getDatabaseFile())) {
				out.write(databaseData);
			}
		}
	}

	private NexuInfo fetchNexuInfo() throws IOException, JAXBException {
		final byte[] info = dataLoader.fetchNexuInfo(serverUrl + "/info");
		if (info == null) {
			return null;
		} else {
			final JAXBContext ctx = JAXBContext.newInstance(NexuInfo.class);
			final Unmarshaller u = ctx.createUnmarshaller();
			try {
				return (NexuInfo) u.unmarshal(new ByteArrayInputStream(info));
			} catch (Exception e) {
				LOGGER.debug("Cannot parse /info", e);
				LOGGER.warn("Cannot parse /info");
				return null;
			}
		}
	}

	public void start() {
		if (database == null) {
			scheduleUpdate();
		} else {
			scheduleTimer();
		}
	}

	public void stop() {
		if (updateHandle != null) {
			updateHandle.cancel(true);
		}
		if (timerHandle != null) {
			timerHandle.cancel(true);
		}
	}

	private void scheduleTimer() {
		timerHandle = scheduler.scheduleAtFixedRate(timerTask, 1, TIMER_RATE, TimeUnit.DAYS);
	}

	private void scheduleUpdate() {
		updateHandle = scheduler.scheduleAtFixedRate(updateTask, 1, UPDATE_RATE, TimeUnit.MINUTES);
	}

	@Override
	public SCDatabase getDatabase() {
		if (database == null) {
			database = new SCDatabase();
		}
		return database;
	}
}
