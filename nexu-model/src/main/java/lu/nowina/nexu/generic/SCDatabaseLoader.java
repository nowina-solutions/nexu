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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import lu.nowina.nexu.TechnicalException;

public class SCDatabaseLoader {

	private static final Logger logger = Logger.getLogger(SCDatabaseLoader.class.getName());

	public static SCDatabase load(File f) {
		SCDatabase db = null;
		if (!f.exists()) {
			db = new SCDatabase();
		} else {
			try (FileInputStream in = new FileInputStream(f)) {
				JAXBContext ctx = createJaxbContext();
				Unmarshaller u = ctx.createUnmarshaller();
				db = (SCDatabase) u.unmarshal(new FileInputStream(f));
			} catch (Exception e) {
				throw new TechnicalException("Cannot load database");
			}
		}
		db.setOnAddAction((data) -> {
			saveAs(data, f);
		});
		return db;
	}

	private static JAXBContext createJaxbContext() {
		try {
			JAXBContext ctx = JAXBContext.newInstance(SCDatabase.class);
			return ctx;
		} catch(JAXBException e) {
			logger.log(Level.SEVERE, "Cannot instanciate JAXBContext", e);
			throw new TechnicalException("Cannot instanciate JAXBContext");
		}
	}

	static void saveAs(SCDatabase db, File file) {
		try {
			JAXBContext ctx = createJaxbContext();
			Marshaller m = ctx.createMarshaller();
			try (FileOutputStream out = new FileOutputStream(file)) {
				m.marshal(db, out);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Cannot save database", e);
			throw new TechnicalException("Cannot save database");
		}
	}

}
