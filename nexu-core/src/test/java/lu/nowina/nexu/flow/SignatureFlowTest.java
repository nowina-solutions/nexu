package lu.nowina.nexu.flow;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.ToBeSigned;
import eu.europa.esig.dss.token.JKSSignatureToken;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import lu.nowina.nexu.api.DetectedCard;
import lu.nowina.nexu.api.Match;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.api.SignatureRequest;
import lu.nowina.nexu.api.SignatureResponse;
import lu.nowina.nexu.api.signature.smartcard.CardAdapter;
import lu.nowina.nexu.api.signature.smartcard.TokenId;
import lu.nowina.nexu.view.core.UIDisplay;

public class SignatureFlowTest {

    @Test
    public void testCardRecognized() {

        UIDisplay display = mock(UIDisplay.class);

        CardAdapter adapter = mock(CardAdapter.class, withSettings().verboseLogging());

        SignatureTokenConnection token = new JKSSignatureToken(this.getClass().getResourceAsStream("/keystore.jks"), "password");

        NexuAPI api = mock(NexuAPI.class, withSettings().verboseLogging());
        DetectedCard detectedCard = new DetectedCard("atr", 0);
        when(api.detectCards()).thenReturn(Arrays.asList(detectedCard));
        when(api.matchingCardAdapters(detectedCard)).thenReturn(Arrays.asList(new Match(adapter, detectedCard)));
        when(api.registerTokenConnection(token)).thenReturn(new TokenId("id"));
        when(api.getTokenConnection(new TokenId("id"))).thenReturn(token);
        
        when(adapter.connect(eq(api), eq(detectedCard), any())).thenReturn(token);

        SignatureRequest req = new SignatureRequest();
        req.setToBeSigned(new ToBeSigned("hello".getBytes()));
        req.setDigestAlgorithm(DigestAlgorithm.SHA256);

        SignatureFlow flow = new SignatureFlow(display);
        SignatureResponse resp = flow.start(api, req);
        Assert.assertNotNull(resp);
        Assert.assertNotNull(resp.getSignatureValue());

    }

    @Test(expected=IllegalArgumentException.class)
    public void testInputValidation1() {

        UIDisplay display = mock(UIDisplay.class);

        NexuAPI api = mock(NexuAPI.class, withSettings().verboseLogging());

        SignatureRequest req = new SignatureRequest();
        req.setDigestAlgorithm(DigestAlgorithm.SHA256);

        SignatureFlow flow = new SignatureFlow(display);
        flow.start(api, req);

    }

    @Test(expected=IllegalArgumentException.class)
    public void testInputValidation2() {

        UIDisplay display = mock(UIDisplay.class);

        NexuAPI api = mock(NexuAPI.class, withSettings().verboseLogging());

        SignatureRequest req = new SignatureRequest();
        req.setToBeSigned(new ToBeSigned());

        SignatureFlow flow = new SignatureFlow(display);
        flow.start(api, req);

    }

    @Test(expected=IllegalArgumentException.class)
    public void testInputValidation3() {

        UIDisplay display = mock(UIDisplay.class);

        NexuAPI api = mock(NexuAPI.class, withSettings().verboseLogging());

        SignatureRequest req = new SignatureRequest();
        req.setToBeSigned(new ToBeSigned("hello".getBytes()));

        SignatureFlow flow = new SignatureFlow(display);
        flow.start(api, req);

    }

}
