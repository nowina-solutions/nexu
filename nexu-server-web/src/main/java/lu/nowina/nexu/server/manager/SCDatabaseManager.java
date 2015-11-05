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

import javax.annotation.PostConstruct;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import lu.nowina.nexu.ConfigurationException;
import lu.nowina.nexu.TechnicalException;

@Service
public class SCDatabaseManager {

	private static final String DIGEST = "MD5";

	private static final Logger logger = LoggerFactory.getLogger(SCDatabaseManager.class.getName());

	@Value("${nexuDatabase}")
	Resource nexuDatabaseFile;

	private byte[] data;

	private String databaseDigest;

	@PostConstruct
	public void postConstruct() {
		if (nexuDatabaseFile == null) {
			throw new ConfigurationException("Configuration must define 'nexuDatabaseFile'");
		}
	}

	public byte[] getData() {

		if (!nexuDatabaseFile.exists()) {
			return new byte[0];
		}

		if (data == null) {
			try (InputStream in = nexuDatabaseFile.getInputStream()) {
				data = IOUtils.toByteArray(in);
			} catch (Exception e) {
				logger.error("Cannot read file " + nexuDatabaseFile, e);
				throw new TechnicalException("Cannot read file " + nexuDatabaseFile);
			}
		}

		return data;
	}

	public String getDatabaseDigest() {

		if (databaseDigest == null) {
			try {
				MessageDigest digest = MessageDigest.getInstance(DIGEST);
				databaseDigest = Hex.encodeHexString(digest.digest(getData()));
			} catch (NoSuchAlgorithmException e) {
				logger.error("Algorithm " + DIGEST + " not found", e);
				throw new TechnicalException("Algorithm " + DIGEST + " not found");
			}
		}

		return databaseDigest;
	}

	public void setNexuDatabaseFile(Resource nexuDatabaseFile) {
		this.nexuDatabaseFile = nexuDatabaseFile;
	}

}
