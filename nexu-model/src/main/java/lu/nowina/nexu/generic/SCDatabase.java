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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lu.nowina.nexu.DatabaseEventHandler;
import lu.nowina.nexu.ProductDatabase;

@XmlRootElement(name = "database")
@XmlAccessorType(XmlAccessType.FIELD)
public class SCDatabase implements ProductDatabase {

	private static final Logger logger = LoggerFactory.getLogger(SCDatabase.class.getName());

	@XmlElement(name = "smartcard")
	private List<SCInfo> smartcards;

	@XmlTransient
	private DatabaseEventHandler onAddAction;

	/**
	 * Add a new ConnectionInfo to the database, associated with the ATR
	 * 
	 * @param detectedAtr
	 * @param cInfo
	 */
	public final void add(String detectedAtr, ConnectionInfo cInfo) {
		SCInfo info = getInfo(detectedAtr);
		if (info == null) {
			info = new SCInfo();
			info.setAtr(detectedAtr);
			getSmartcards0().add(info);
		}
		info.getInfos().add(cInfo);
		onAdd();
	}

	private void onAdd() {
		if(onAddAction != null) {
			onAddAction.execute(this);
		} else {
			logger.warn("No DatabaseEventHandler define, the database cannot be stored");
		}
	}

	/**
	 * Get SCInfo matching the provided ATR
	 * 
	 * @param atr
	 * @return
	 */
	public SCInfo getInfo(String atr) {
		for (SCInfo i : getSmartcards()) {
			if (i.getAtr().equals(atr)) {
				return i;
			}
		}
		return null;
	}

	private List<SCInfo> getSmartcards0() {
		if (smartcards == null) {
			this.smartcards = new ArrayList<>();
		}
		return smartcards;
	}
	
	public List<SCInfo> getSmartcards() {
		return Collections.unmodifiableList(getSmartcards0());
	};

	@Override
	public void setOnAddRemoveAction(DatabaseEventHandler onAddAction) {
		this.onAddAction = onAddAction;
	}

}
