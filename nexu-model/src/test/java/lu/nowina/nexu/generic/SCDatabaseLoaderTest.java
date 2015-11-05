package lu.nowina.nexu.generic;

import java.io.File;

import javax.xml.bind.JAXBContext;

import org.junit.Test;

public class SCDatabaseLoaderTest {

	@Test
	public void test1() throws Exception {

		SCDatabase db = SCDatabaseLoader.load(new File("src/test/resources/db.xml"));

		JAXBContext ctx = JAXBContext.newInstance(SCDatabase.class);
		ctx.createMarshaller().marshal(db, System.out);

		SCDatabaseLoader.saveAs(db, new File("target/db.xml"));

	}

}
