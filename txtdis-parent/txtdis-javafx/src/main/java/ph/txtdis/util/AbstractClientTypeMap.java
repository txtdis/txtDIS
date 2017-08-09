package ph.txtdis.util;

import org.springframework.core.ParameterizedTypeReference;
import ph.txtdis.dto.AgingReceivableReport;
import ph.txtdis.dto.CustomerReceivableReport;

public abstract class AbstractClientTypeMap //
	extends AbstractTypeMap //
	implements ClientTypeMap {

	private static final long serialVersionUID = -1782679034968493608L;

	public AbstractClientTypeMap() {
		put("adjustment", new TypeReference("\ue92b", null));
		put("agingReceivable", new TypeReference("\ue900", new ParameterizedTypeReference<AgingReceivableReport>() {
		}));
		put("back", new TypeReference("\ue902", null));
		put("credit", new TypeReference("\ue91b", null));
		put("customerReceivable", new TypeReference("\ue900", new ParameterizedTypeReference<CustomerReceivableReport>
			() {
		}));
	}

	@Override
	public String icon(String name) {
		TypeReference type = get(name);
		return type == null ? null : type.getIcon();
	}
}
