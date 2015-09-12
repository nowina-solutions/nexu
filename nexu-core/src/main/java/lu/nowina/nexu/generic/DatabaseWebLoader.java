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
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;

public class DatabaseWebLoader {

    private static final Logger logger = Logger.getLogger(DatabaseWebLoader.class.getName());

    private byte[] databaseData;

    private SCDatabase database;

    private String serverUrl;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private ScheduledFuture<?> timerHandle;

    private ScheduledFuture<?> updateHandle;

    private final int TIMER_RATE = 1;

    private final int UPDATE_RATE = 10;

    private HttpDataLoader dataLoader;

    public DatabaseWebLoader(String serverUrl, HttpDataLoader dataLoader) throws IOException {
        this.serverUrl = serverUrl;
        this.dataLoader = dataLoader;

        try {
            loadDatabaseFromCache();
        } catch (IOException | JAXBException e) {
            logger.log(Level.SEVERE, "Cannot load database from cache", e);
        }

        try {
            updateDatabase();
        } catch (IOException | JAXBException e) {
            logger.log(Level.SEVERE, "Cannot update database", e);
        }

    }

    public void loadDatabaseFromCache() throws IOException, JAXBException {

        File databaseWeb = new File("./database-web.xml");
        if (databaseWeb.exists()) {
            try (FileInputStream in = new FileInputStream(databaseWeb)) {
                databaseData = IOUtils.toByteArray(in);
                parseDatabase();
            }
        }

    }

    private SCDatabase parseDatabase() throws JAXBException {
        JAXBContext ctx = JAXBContext.newInstance(SCDatabase.class);
        Unmarshaller u = ctx.createUnmarshaller();
        return (SCDatabase) u.unmarshal(new ByteArrayInputStream(databaseData));
    }

    private String digestDatabase() {

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return new String(Hex.encodeHex(md.digest(databaseData)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    private void updateDatabase() throws JAXBException, IOException {
        NexuInfo info = fetchNexuInfo();
        if (info != null && !info.getDatabaseVersion().equals(digestDatabase())) {
            fetchDatabase();
        }
    }

    public void fetchDatabase() throws IOException {

        databaseData = dataLoader.fetchDatabase(serverUrl + "/database.xml");

    }

    public NexuInfo fetchNexuInfo() throws IOException, JAXBException {

        byte[] info = dataLoader.fetchNexuInfo(serverUrl + "/info");

        if (info == null) {
            return null;
        } else {
            JAXBContext ctx = JAXBContext.newInstance(NexuInfo.class);
            Unmarshaller u = ctx.createUnmarshaller();
            NexuInfo version = (NexuInfo) u.unmarshal(new ByteArrayInputStream(info));
            return version;
        }

    }

    public void start() {
        scheduleTimer();
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

    public SCDatabase getDatabase() {
        if (database == null) {
            database = new SCDatabase();
        }
        return database;
    }

    final Runnable timerTask = new Runnable() {
        public void run() {
            if (updateHandle != null) {
                logger.info("Update still running");
            } else {
                logger.info("Schedule update");
                scheduleUpdate();
            }
        }

    };

    final Runnable updateTask = new Runnable() {
        public void run() {
            logger.info("Attempt update");
            try {
                updateDatabase();
                scheduleTimer();
                updateHandle.cancel(false);
                updateHandle = null;
            } catch (IOException | JAXBException e) {
                logger.log(Level.SEVERE, "Cannot update database", e);
            }
        }

    };

}
