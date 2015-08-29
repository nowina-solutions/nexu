package lu.nowina.nexu.flow;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import lu.nowina.nexu.api.DetectedCard;
import lu.nowina.nexu.api.Feedback;
import lu.nowina.nexu.api.GetCertificateRequest;
import lu.nowina.nexu.api.GetCertificateResponse;
import lu.nowina.nexu.api.NexuAPI;
import lu.nowina.nexu.view.core.UIDisplay;

public class GetCertificateFlowTest {

    @Test
    public void testNoProduct() {
        
        UIDisplay display = mock(UIDisplay.class); 

        NexuAPI api = mock(NexuAPI.class);
        when(api.detectCards()).thenReturn(Collections.emptyList());
        
        GetCertificateRequest req = new GetCertificateRequest();
        
        GetCertificateFlow flow = new GetCertificateFlow(display);
        GetCertificateResponse resp = flow.start(api, req);
        Assert.assertNull(resp);

        verify(display, atLeastOnce()).displayAndWaitUIOperation(eq("/fxml/provide-feedback.fxml"), any(Feedback.class));
    }
    
    @Test
    public void testNotRecognizedRequestSupport() {
        
        UIDisplay display = mock(UIDisplay.class); 
        when(display.displayAndWaitUIOperation(eq("/fxml/unsupported-product.fxml"))).thenReturn(false);

        NexuAPI api = mock(NexuAPI.class);
        when(api.detectCards()).thenReturn(Arrays.asList(new DetectedCard("atr", 0)));
        
        GetCertificateRequest req = new GetCertificateRequest();
        
        GetCertificateFlow flow = new GetCertificateFlow(display);
        GetCertificateResponse resp = flow.start(api, req);
        Assert.assertNull(resp);
        
        verify(display, atLeastOnce()).displayAndWaitUIOperation(eq("/fxml/provide-feedback.fxml"), any(Feedback.class));
    }
    
}
