/**
 * Â© Nowina Solutions, 2015-2015
 *
 * ConceÌ�deÌ�e sous licence EUPL, version 1.1 ou â€“ deÌ€s leur approbation par la Commission europeÌ�enne - versions ulteÌ�rieures de lâ€™EUPL (la Â«LicenceÂ»).
 * Vous ne pouvez utiliser la preÌ�sente Å“uvre que conformeÌ�ment aÌ€ la Licence.
 * Vous pouvez obtenir une copie de la Licence aÌ€ lâ€™adresse suivante:
 *
 * http://ec.europa.eu/idabc/eupl5
 *
 * Sauf obligation leÌ�gale ou contractuelle eÌ�crite, le logiciel distribueÌ� sous la Licence est distribueÌ� Â«en lâ€™eÌ�tatÂ»,
 * SANS GARANTIES OU CONDITIONS QUELLES QUâ€™ELLES SOIENT, expresses ou implicites.
 * Consultez la Licence pour les autorisations et les restrictions linguistiques speÌ�cifiques relevant de la Licence.
 */
package lu.nowina.nexu.api;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;

import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.ToBeSigned;
import lu.nowina.nexu.json.GsonHelper;

public class SignatureRequestTest {

	@Test
	public void test1() {

		SignatureRequest obj = new SignatureRequest();
		obj.setDigestAlgorithm(DigestAlgorithm.SHA1);
		obj.setKeyId("key");

		Gson gson = new Gson();
		String text = gson.toJson(obj);

		Assert.assertEquals("{\"digestAlgorithm\":\"SHA1\",\"keyId\":\"key\"}", text);

		SignatureRequest obj2 = gson.fromJson(text, SignatureRequest.class);

		Assert.assertEquals(obj.getKeyId(), obj2.getKeyId());
		Assert.assertEquals(obj.getDigestAlgorithm(), obj2.getDigestAlgorithm());

	}

	@Test
	public void test2() {

		SignatureRequest obj = new SignatureRequest();
		obj.setDigestAlgorithm(DigestAlgorithm.SHA1);
		obj.setKeyId("key");

		ToBeSigned tbs = new ToBeSigned("HelloWorld".getBytes());
		obj.setToBeSigned(tbs);
		obj.setTokenId(new TokenId("tokenId"));

		String text = GsonHelper.toJson(obj);

		System.out.println(text);

		SignatureRequest obj2 = GsonHelper.fromJson(text, SignatureRequest.class);

		Assert.assertEquals(obj.getKeyId(), obj2.getKeyId());
		Assert.assertEquals(obj.getDigestAlgorithm(), obj2.getDigestAlgorithm());

	}

}
