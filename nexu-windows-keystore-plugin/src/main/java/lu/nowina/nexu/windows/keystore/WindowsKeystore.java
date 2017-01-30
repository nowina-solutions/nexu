package lu.nowina.nexu.windows.keystore;

import java.util.ResourceBundle;

import org.apache.commons.lang.StringEscapeUtils;

import lu.nowina.nexu.api.Product;

/**
 * Represents a Windows keystore.
 * 
 * @author simon.ghisalberti
 *
 */
public class WindowsKeystore implements Product {

	public WindowsKeystore() {
		super();
	}

	@Override
	public String getLabel() {
		return StringEscapeUtils.unescapeJava(ResourceBundle.getBundle("bundles/nexu").getString("product.selection.windows.keystore"));
	}

}
