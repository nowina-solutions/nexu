package lu.nowina.nexu.windows.keystore;

import java.util.ResourceBundle;

import org.apache.commons.lang.StringEscapeUtils;

import lu.nowina.nexu.api.Product;

public class WindowsKeystore implements Product {

	public WindowsKeystore() {
		super();
	}

	@Override
	public String getLabel() {
		return StringEscapeUtils.unescapeJava(ResourceBundle.getBundle("bundles/nexu").getString("product.selection.windows.keystore"));
	}

}
