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
import java.io.FileOutputStream;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lu.nowina.nexu.TechnicalException;

public class SCDatabaseLoader {

	private static final Logger logger = LoggerFactory.getLogger(SCDatabaseLoader.class.getName());

	public static SCDatabase load(final File f) {
		SCDatabase db = null;
		if (!f.exists()) {
			db = new SCDatabase();
		} else {
			try {
				final JAXBContext ctx = createJaxbContext();
				final Unmarshaller u = ctx.createUnmarshaller();
				db = (SCDatabase) u.unmarshal(f);
			} catch (final Exception e) {
				throw new TechnicalException("Cannot load database", e);
			}
		}
		db.setOnAddAction(new DatabaseEventHandler() {
			@Override
			public void execute(SCDatabase data) {
				saveAs(data, f);
			}
		});
		return db;
	}

	public static SCDatabase load(final URL url) {
		try {
			final JAXBContext ctx = createJaxbContext();
			final Unmarshaller u = ctx.createUnmarshaller();
			return (SCDatabase) u.unmarshal(url);
		} catch (final Exception e) {
			throw new TechnicalException("Cannot load database", e);
		}
	}
	
	private static JAXBContext createJaxbContext() {
		try {
			JAXBContext ctx = JAXBContext.newInstance(SCDatabase.class);
			return ctx;
		} catch (JAXBException e) {
			logger.error("Cannot instanciate JAXBContext", e);
			throw new TechnicalException("Cannot instanciate JAXBContext", e);
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
			logger.error("Cannot save database", e);
			throw new TechnicalException("Cannot save database", e);
		}
	}
}
