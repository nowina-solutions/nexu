package lu.nowina.nexu.https;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class HttpsPluginTest {

	File target;

	@Before
	public void before() {
		target = new File("./target");
		System.setProperty("user.home", target.getAbsolutePath());
	}

	@Test
	public void test2() throws Exception {
		new HttpsPlugin().createRootCAKeystore(target, "NexU", new PKIManager());
	}
}
