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
package lu.nowina.nexu;

import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;

import lu.nowina.nexu.api.AppConfig;
import lu.nowina.nexu.api.ProductAdapter;
import lu.nowina.nexu.api.DetectedCard;
import lu.nowina.nexu.api.EnvironmentInfo;
import lu.nowina.nexu.api.Match;
import lu.nowina.nexu.view.core.UIDisplay;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;

public class InternalAPITest {

	@Test
	public void testEnvironment() throws Exception {

		InternalAPI api = new InternalAPI(null, null, null, null, null, mock(AppConfig.class));

		EnvironmentInfo info = api.getEnvironmentInfo();
		Assert.assertNotNull(info.getOs());
		Assert.assertNotNull(info.getArch());
		Assert.assertNotNull(info.getOsName());
		Assert.assertNotNull(info.getOsVersion());
		Assert.assertNotNull(info.getJreVendor());

	}

	/**
	 * No card are detected
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDetectCards1() throws Exception {

		CardDetector detector = Mockito.mock(CardDetector.class);

		UIDisplay display = Mockito.mock(UIDisplay.class);

		InternalAPI api = new InternalAPI(display, null, detector, null, null, mock(AppConfig.class));
		Assert.assertEquals(0, api.detectCards().size());

	}

	@Test
	public void testDetectCards2() throws Exception {

		CardDetector detector = Mockito.mock(CardDetector.class);
		Mockito.when(detector.detectCard()).thenReturn(Arrays.asList(new DetectedCard("ATR", 0)));

		UIDisplay display = Mockito.mock(UIDisplay.class);

		InternalAPI api = new InternalAPI(display, null, detector, null, null, mock(AppConfig.class));
		Assert.assertEquals(1, api.detectCards().size());

	}

	@Test
	public void testDetectCards3() throws Exception {

		CardDetector detector = Mockito.mock(CardDetector.class);
		Mockito.when(detector.detectCard()).thenReturn(Arrays.asList(new DetectedCard("ATR1", 0), new DetectedCard("ATR2", 0)));

		UIDisplay display = Mockito.mock(UIDisplay.class);

		InternalAPI api = new InternalAPI(display, null, detector, null, null, mock(AppConfig.class));
		Assert.assertEquals(2, api.detectCards().size());

	}

	@Test
	public void testMatchingProductAdapter1() throws Exception {

		DetectedCard card = new DetectedCard("ATR", 0);

		UIDisplay display = Mockito.mock(UIDisplay.class);
		InternalAPI api = new InternalAPI(display, null, null, null, null, mock(AppConfig.class));

		SignatureTokenConnection c = new MockSignatureTokenConnection((DSSPrivateKeyEntry[])null);
		api.registerTokenConnection(c);

		ProductAdapter adapter1 = Mockito.mock(ProductAdapter.class);
		Mockito.when(adapter1.accept(card)).thenReturn(Boolean.TRUE);
		Mockito.when(adapter1.connect(api, card, display.getPasswordInputCallback())).thenReturn(c);

		List<Match> matches = api.matchingProductAdapters(card);

		Assert.assertEquals(0, matches.size());

		api.registerProductAdapter(adapter1);

		matches = api.matchingProductAdapters(card);

		Assert.assertEquals(1, matches.size());

	}

}
