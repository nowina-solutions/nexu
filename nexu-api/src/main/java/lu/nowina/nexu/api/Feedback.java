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
package lu.nowina.nexu.api;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "feedback", propOrder = { "apiParameter", "detected", "feedbackStatus", "selectedAPI", "selectedCard", "stacktrace", "userComment", "info" })
public class Feedback {

	protected String apiParameter;
	@XmlElement(nillable = true)
	protected List<DetectedCard> detected;
	protected FeedbackStatus feedbackStatus;
	protected ScAPI selectedAPI;
	protected DetectedCard selectedCard;
	protected String stacktrace;
	protected String userComment;
	protected EnvironmentInfo info;

	public Feedback() {
	}

	public Feedback(Exception e) {

		StringWriter buffer = new StringWriter();
		PrintWriter writer = new PrintWriter(buffer);
		e.printStackTrace(writer);
		writer.close();

		setStacktrace(buffer.toString());
		setFeedbackStatus(FeedbackStatus.EXCEPTION);
	}

	/**
	 * Gets the value of the apiParameter property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getApiParameter() {
		return apiParameter;
	}

	/**
	 * Sets the value of the apiParameter property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setApiParameter(String value) {
		this.apiParameter = value;
	}

	/**
	 * Gets the value of the detected property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present
	 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method for the detected property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getDetected().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link DetectedCard }
	 * 
	 * 
	 */
	public List<DetectedCard> getDetected() {
		if (detected == null) {
			detected = new ArrayList<DetectedCard>();
		}
		return this.detected;
	}

	/**
	 * Gets the value of the feedbackStatus property.
	 * 
	 * @return possible object is {@link FeedbackStatus }
	 * 
	 */
	public FeedbackStatus getFeedbackStatus() {
		return feedbackStatus;
	}

	/**
	 * Sets the value of the feedbackStatus property.
	 * 
	 * @param value
	 *            allowed object is {@link FeedbackStatus }
	 * 
	 */
	public void setFeedbackStatus(FeedbackStatus value) {
		this.feedbackStatus = value;
	}

	/**
	 * Gets the value of the selectedAPI property.
	 * 
	 * @return possible object is {@link ScAPI }
	 * 
	 */
	public ScAPI getSelectedAPI() {
		return selectedAPI;
	}

	/**
	 * Sets the value of the selectedAPI property.
	 * 
	 * @param value
	 *            allowed object is {@link ScAPI }
	 * 
	 */
	public void setSelectedAPI(ScAPI value) {
		this.selectedAPI = value;
	}

	/**
	 * Gets the value of the selectedCard property.
	 * 
	 * @return possible object is {@link DetectedCard }
	 * 
	 */
	public DetectedCard getSelectedCard() {
		return selectedCard;
	}

	/**
	 * Sets the value of the selectedCard property.
	 * 
	 * @param value
	 *            allowed object is {@link DetectedCard }
	 * 
	 */
	public void setSelectedCard(DetectedCard value) {
		this.selectedCard = value;
	}

	/**
	 * Gets the value of the stacktrace property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getStacktrace() {
		return stacktrace;
	}

	/**
	 * Sets the value of the stacktrace property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setStacktrace(String value) {
		this.stacktrace = value;
	}

	public String getUserComment() {
		return userComment;
	}

	public void setUserComment(String userComment) {
		this.userComment = userComment;
	}

	public EnvironmentInfo getInfo() {
		return info;
	}

	public void setInfo(EnvironmentInfo info) {
		this.info = info;
	}

}
