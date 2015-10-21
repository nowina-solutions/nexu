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
package lu.nowina.nexu.server.manager;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class SCDatabaseManager {
	
	private static final Logger logger = Logger.getLogger(SCDatabaseManager.class.getName());

	@Value("${nexuDatabase}")
	Resource nexuDatabaseFile;

	private byte[] data;

	private String databaseDigest;

	public byte[] getData() {

		if (!nexuDatabaseFile.exists()) {
			return new byte[0];
		}

		if (data == null) {
			try (InputStream in = nexuDatabaseFile.getInputStream()) {
				data = IOUtils.toByteArray(in);
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Cannot read file " + nexuDatabaseFile, e);
				throw new RuntimeException(e);
			}
		}

		return data;
	}

	public String getDatabaseDigest() {

		if (databaseDigest == null) {
			try {
				MessageDigest digest = MessageDigest.getInstance("MD5");
				databaseDigest = Hex.encodeHexString(digest.digest(getData()));
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
		}

		return databaseDigest;
	}

}
