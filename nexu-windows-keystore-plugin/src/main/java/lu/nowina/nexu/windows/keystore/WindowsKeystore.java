package lu.nowina.nexu.windows.keystore;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringEscapeUtils;

import lu.nowina.nexu.api.Product;

public class WindowsKeystore implements Product {

	private String atr;

	public WindowsKeystore() {
		super();
	}

	public WindowsKeystore(String atr) {
		super();
		this.atr = atr;
	}

	public String getAtr() {
		return atr;
	}

	public void setAtr(String atr) {
		this.atr = atr;
	}

	@Override
	public String getLabel() {
		return StringEscapeUtils
				.unescapeJava(MessageFormat.format(ResourceBundle.getBundle("bundles/nexu").getString("product.selection.windows.keystore"), this.getAtr()));
	}

}
