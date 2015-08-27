package lu.nowina.nexu;

import lu.nowina.nexu.api.EnvironmentInfo;

import org.junit.Assert;
import org.junit.Test;

public class InternalAPITest {

	@Test
	public void test1() throws Exception {

		InternalAPI api = new InternalAPI(null, null, null);

		EnvironmentInfo info = api.getEnvironmentInfo();
		Assert.assertNotNull(info.getOs());
		Assert.assertNotNull(info.getArch());

	}

}
