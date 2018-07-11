package lu.nowina.nexu.api;

import java.util.ArrayList;
import java.util.List;

import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.tsl.KeyUsageBit;
import lu.nowina.nexu.api.CertificateFilter;

/**
 * Provides filtering capabilities for product adapters.
 * 
 * @author Landry Soules
 *
 */
public class CertificateFilterHelper {

	public List<DSSPrivateKeyEntry> filterKeys(SignatureTokenConnection token, CertificateFilter filter) {
		if (filter.getNonRepudiationBit()) {
			List<DSSPrivateKeyEntry> filteredList = new ArrayList<>();
			for (DSSPrivateKeyEntry entry : token.getKeys()) {
				if (entry.getCertificate().checkKeyUsage(KeyUsageBit.nonRepudiation)) {
					filteredList.add(entry);
				}
			}
			return filteredList;
		}
		return token.getKeys();
	}
}
