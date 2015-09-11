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
package lu.nowina.nexu.generic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class SCDatabaseLoader {

	private static final Logger logger = Logger.getLogger(SCDatabaseLoader.class.getName());

	public static SCDatabase load(File f) throws Exception {
		SCDatabase db = null;
		if (f.exists()) {
			JAXBContext ctx = JAXBContext.newInstance(SCDatabase.class);
			Unmarshaller u = ctx.createUnmarshaller();
			try (FileInputStream in = new FileInputStream(f)) {
				db = (SCDatabase) u.unmarshal(new FileInputStream(f));
			}
		} else {
			db = new SCDatabase();
		}
		db.setOnAddAction((data) -> {
			saveAs(data,f);
		});
		return db;
	}

	private static void saveAs(SCDatabase db, File file) {
		try {
			JAXBContext ctx = JAXBContext.newInstance(SCDatabaseLoader.class);
			Marshaller m = ctx.createMarshaller();
			try (FileOutputStream out = new FileOutputStream(file)) {
				m.marshal(db, out);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
