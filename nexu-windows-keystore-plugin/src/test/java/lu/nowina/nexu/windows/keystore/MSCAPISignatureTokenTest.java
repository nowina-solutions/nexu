package lu.nowina.nexu.windows.keystore;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.MSCAPISignatureToken;

//TODO : delete JUnitTest not consistent, depend on window
public class MSCAPISignatureTokenTest {

	@Test
	public void getMSCAPISignatureToken() {
		MSCAPISignatureToken token = new MSCAPISignatureToken();
		List<DSSPrivateKeyEntry> keyEntries = token.getKeys();
		Assert.assertNotNull(token);
		Assert.assertEquals(1, token.getKeys());
		Assert.assertNotNull(keyEntries);
	}
}
