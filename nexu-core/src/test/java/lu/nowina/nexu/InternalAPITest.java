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

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import eu.europa.esig.dss.token.SignatureTokenConnection;
import lu.nowina.nexu.api.DetectedCard;
import lu.nowina.nexu.api.EnvironmentInfo;
import lu.nowina.nexu.api.Match;
import lu.nowina.nexu.api.signature.smartcard.CardAdapter;
import lu.nowina.nexu.api.signature.smartcard.TokenId;
import lu.nowina.nexu.smartcard.dector.CardDetector;
import lu.nowina.nexu.view.core.UIDisplay;

public class InternalAPITest {

    @Test
    public void testEnvironment() throws Exception {

        InternalAPI api = new InternalAPI(null, null, null, null);

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

        InternalAPI api = new InternalAPI(display, null, null, detector);
        Assert.assertEquals(0, api.detectCards().size());

    }

    @Test
    public void testDetectCards2() throws Exception {

        CardDetector detector = Mockito.mock(CardDetector.class);
        Mockito.when(detector.detectCard()).thenReturn(Arrays.asList(new DetectedCard("ATR", 0)));

        UIDisplay display = Mockito.mock(UIDisplay.class);

        InternalAPI api = new InternalAPI(display, null, null, detector);
        Assert.assertEquals(1, api.detectCards().size());

    }

    @Test
    public void testDetectCards3() throws Exception {

        CardDetector detector = Mockito.mock(CardDetector.class);
        Mockito.when(detector.detectCard())
                .thenReturn(Arrays.asList(new DetectedCard("ATR1", 0), new DetectedCard("ATR2", 0)));

        UIDisplay display = Mockito.mock(UIDisplay.class);

        InternalAPI api = new InternalAPI(display, null, null, detector);
        Assert.assertEquals(2, api.detectCards().size());

    }

    @Test
    public void testMatchingCardAdapter1() throws Exception {

        DetectedCard card = new DetectedCard("ATR", 0);

        UIDisplay display = Mockito.mock(UIDisplay.class);
        InternalAPI api = new InternalAPI(display, null, null, null);

        SignatureTokenConnection c = new MockSignatureTokenConnection(null);
        TokenId id = api.registerTokenConnection(c);

        CardAdapter adapter1 = Mockito.mock(CardAdapter.class);
        Mockito.when(adapter1.accept(card)).thenReturn(Boolean.TRUE);
        Mockito.when(adapter1.connect(api, card, display.getPasswordInputCallback())).thenReturn(c);

        List<Match> matches = api.matchingCardAdapters(card);

        Assert.assertEquals(0, matches.size());

        api.registerCardAdapter(adapter1);

        matches = api.matchingCardAdapters(card);

        Assert.assertEquals(1, matches.size());

    }

}