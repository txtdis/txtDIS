package ph.txtdis.util;

import org.springframework.core.ParameterizedTypeReference;

public class TypeReference {

	private String icon;

	private ParameterizedTypeReference<?> type;

	public TypeReference(String icon, ParameterizedTypeReference<?> type) {
		this.icon = icon;
		this.type = type;
	}

	public String getIcon() {
		return icon;
	}

	public ParameterizedTypeReference<?> getType() {
		return type;
	}
}
