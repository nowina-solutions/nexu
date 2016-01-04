package lu.nowina.nexu.rest;

import lu.nowina.nexu.api.flow.OperationStatus;

/**
 * This enum gathers all possible {@link OperationStatus}es for the REST HTTP plugin.
 *
 * @author Jean Lepropre (jean.lepropre@nowina.lu)
 */
public enum RestPluginOperationStatus implements OperationStatus {

	NOT_SUPPORTED_ONLY_ENCRYPTION_REQUIRED("not.supported.only.encryption.required",
			"The REST HTTP plugin does not support the OnlyEncryptionRequired parameter.");
	
	private final String code;
	private final String label;
	
	private RestPluginOperationStatus(final String code, final String label) {
		this.code = code;
		this.label = label;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getLabel() {
		return label;
	}
}
