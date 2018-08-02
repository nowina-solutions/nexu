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
package lu.nowina.nexu.api.plugin;

/**
 * POJO that holds information about an event that occurred during the initialization of a {@link NexuPlugin}.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public class InitializationMessage {
	
	private final MessageType messageType;
	private final String title;
	private final String headerText;
	private final String contentText;

	public InitializationMessage(MessageType messageType, String title, String headerText, String contentText) {
		super();
		this.messageType = messageType;
		this.title = title;
		this.headerText = headerText;
		this.contentText = contentText;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public String getTitle() {
		return title;
	}

	public String getHeaderText() {
		return headerText;
	}

	public String getContentText() {
		return contentText;
	}

	public static enum MessageType {
		WARNING;
	}
}
