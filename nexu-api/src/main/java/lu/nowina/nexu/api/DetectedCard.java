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

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringEscapeUtils;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "detectedCard", propOrder = { "atr", "terminalIndex" })
public class DetectedCard implements Product {

	/**
	 * The atr.
	 */
	private String atr;

	/**
	 * The terminal index.
	 */
	private int terminalIndex;

	/**
	 * The terminal label.
	 */
	@XmlTransient
	private String terminalLabel;
	
	public DetectedCard() {
	}

	public DetectedCard(String atr, int terminalIndex) {
		this.atr = atr;
		this.terminalIndex = terminalIndex;
	}
	
	public DetectedCard(String atr, int terminalIndex, String terminalLabel) {
		super();
		this.atr = atr;
		this.terminalIndex = terminalIndex;
		this.terminalLabel = terminalLabel;
	}

	/**
	 * Transform an ATR byte array into a string.
	 *
	 * @param b
	 *            the ATR byte array
	 * @return the string (empty if the ATR byte array is empty or null)
	 */
	public static String atrToString(byte[] b) {
		final StringBuilder sb = new StringBuilder();
		if (b != null && b.length > 0) {
			sb.append(Integer.toHexString((b[0] & 240) >> 4));
			sb.append(Integer.toHexString(b[0] & 15));

			for (int i = 1; i < b.length; i++) {
				// sb.append(' ');
				sb.append(Integer.toHexString((b[i] & 240) >> 4));
				sb.append(Integer.toHexString(b[i] & 15));
			}
		}
		return sb.toString().toUpperCase();
	}

	/**
	 * Gets the atr.
	 *
	 * @return the atr
	 */
	public String getAtr() {
		return atr;
	}

	/**
	 * Sets the atr.
	 *
	 * @param atr
	 *            the atr to set
	 */
	public void setAtr(String atr) {
		this.atr = atr;
	}

	/**
	 * Get the index of the terminal from which the card info was read.
	 *
	 * @return the terminalIndex
	 */
	public int getTerminalIndex() {
		return terminalIndex;
	}

	/**
	 * Set the index of the terminal from which the card info was read.
	 *
	 * @param terminalIndex
	 *            the terminalIndex to set
	 */
	public void setTerminalIndex(int terminalIndex) {
		this.terminalIndex = terminalIndex;
	}

	/**
	 * Returns the label of the terminal from which the card info was read.
	 * @return The label of the terminal from which the card info was read.
	 */
	public String getTerminalLabel() {
		return terminalLabel;
	}

	/**
	 * Sets the label of the terminal from which the card info was read.
	 * @param terminalLabel The label of the terminal from which the card info was read.
	 */
	public void setTerminalLabel(String terminalLabel) {
		this.terminalLabel = terminalLabel;
	}

	@Override
	public String getLabel() {
		return StringEscapeUtils.unescapeJava(MessageFormat.format(
				ResourceBundle.getBundle("bundles/nexu").getString("product.selection.detected.card.button.label"),
				this.getTerminalIndex(), this.getTerminalLabel(), this.getAtr()));
	}
}
