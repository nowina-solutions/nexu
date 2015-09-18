package lu.nowina.nexu.server.manager;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SCDatabaseManager {

	@Value("${nexuDatabase}")
	private File nexuDatabaseFile;

	private byte[] data;

	private String databaseDigest;

	public byte[] getData() {

		if (!nexuDatabaseFile.exists()) {
			return new byte[0];
		}

		if (data == null) {
			try (FileInputStream in = new FileInputStream(nexuDatabaseFile)) {
				data = IOUtils.toByteArray(in);
			} catch (Exception e) {
				throw new RuntimeException();
			}
		}

		return data;
	}

	public String getDatabaseDigest() {

		if (databaseDigest == null) {
			try {
				MessageDigest digest = MessageDigest.getInstance("MD5");
				databaseDigest = Hex.encodeHexString(digest.digest(getData()));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		return databaseDigest;
	}

}
