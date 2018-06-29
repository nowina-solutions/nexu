package lu.nowina.nexu.flow;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ResourceBundle.class, StageHelper.class})
public class StageHelperTest {

	private ResourceBundle rb;
	private StageHelper sh;
	private static final String KEY_EXISTS = "key.exists";
	private static final String KEY_DOES_NOT_EXISTS = "key.does.not.exist";

	@Before
	public void init() {
		rb = PowerMockito.mock(ResourceBundle.class);
		PowerMockito.when(rb.getString(KEY_EXISTS)).thenReturn("ok");
		PowerMockito.when(rb.getString(KEY_DOES_NOT_EXISTS))
				.thenThrow(new MissingResourceException("dummy1", "dummy2", "dummy3"));
		sh = PowerMockito.spy(StageHelper.getInstance());
		when(sh.getBundle()).thenReturn(rb);
	}

	@Test
	public void testApplicationNameIsNull() {
		sh.setTitle(null, KEY_EXISTS);
		assertEquals("ok", sh.getTitle());
		
	}

	@Test
	public void testTitleIsNull() {
		sh.setTitle("Fake Company", null);
		assertEquals("Fake Company", sh.getTitle());
	}

	@Test
	public void testKeyNotFound() {
		sh.setTitle("Fake Company", KEY_DOES_NOT_EXISTS);
		assertEquals("Fake Company", sh.getTitle());
	}

	@Test
	public void testOk() {
		sh.setTitle("Fake Company", KEY_EXISTS);
		assertEquals("Fake Company - ok", sh.getTitle());
	}

}
