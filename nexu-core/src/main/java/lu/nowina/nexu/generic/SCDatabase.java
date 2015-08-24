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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import lu.nowina.nexu.api.ScAPI;

@XmlRootElement
public class SCDatabase {

	private static final Logger logger = Logger.getLogger(SCDatabase.class.getName());
	
	private File file;
	
	private List<SCInfo> smartcards;
	
	public void add(String detectedAtr, ScAPI selectedApi, String apiParam) {
		SCInfo info = new SCInfo();
		info.setApiParam(apiParam);
		info.setDetectedAtr(detectedAtr);
		info.setSelectedApi(selectedApi);
		smartcards.add(info);
		try {
			save();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Cannot save", e);
		}
	}
	
	public SCInfo getInfo(String detectedAtr) {
		for(SCInfo i : getSmartcards()) {
			if(i.getDetectedAtr().equals(detectedAtr)) {
				return i;
			}
		}
		return null;
	}
	
	public static SCDatabase load(File f) throws Exception {
		if(f.exists()) {
			JAXBContext ctx = JAXBContext.newInstance(SCDatabase.class);
			Unmarshaller u = ctx.createUnmarshaller();
			try (FileInputStream in = new FileInputStream(f)) {
				SCDatabase db = (SCDatabase) u.unmarshal(new FileInputStream(f));
				db.file = f;
				return db;
			}
		} else {
			SCDatabase db = new SCDatabase();
			db.file = f;
			return db;
		}
	}
	
	public void save() throws Exception {
		JAXBContext ctx = JAXBContext.newInstance(SCDatabase.class);
		Marshaller m = ctx.createMarshaller();
		try (FileOutputStream out = new FileOutputStream(file)) {
			m.marshal(this, out);
		}
	}

	public List<SCInfo> getSmartcards() {
		if(smartcards == null) {
			this.smartcards = new ArrayList<>();
		}
		return smartcards;
	}
	
	public void setSmartcards(List<SCInfo> smartcards) {
		this.smartcards = smartcards;
	}
}
