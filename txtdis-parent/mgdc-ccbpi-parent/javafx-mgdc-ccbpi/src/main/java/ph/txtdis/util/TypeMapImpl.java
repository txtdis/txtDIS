package ph.txtdis.util;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.PickList;
import ph.txtdis.dto.SalesItemVariance;

@Component("typeMap")
public class TypeMapImpl extends AbstractClientTypeMap {

	private static final long serialVersionUID = -5360838591923833592L;

	public TypeMapImpl() {
		put("bookingVariance", new TypeReference("\ue904", null));
		put("bookingVariances", new TypeReference(null, new ParameterizedTypeReference<List<SalesItemVariance>>() {
		}));
		put("blanketBalance", new TypeReference("\ue910", null));
		put("blanketBalances", new TypeReference(null, new ParameterizedTypeReference<List<SalesItemVariance>>() {
		}));
		put("deliveryList", new TypeReference("\ue931", new ParameterizedTypeReference<Billable>() {
		}));
		put("deliveryReturn", new TypeReference("\ue954", null));
		put("deliveryVariances", new TypeReference(null, new ParameterizedTypeReference<List<SalesItemVariance>>() {
		}));
		put("loadIn", new TypeReference("\ue952", null));
		put("loadManifest", new TypeReference("\ue90b", new ParameterizedTypeReference<Billable>() {
		}));
		put("loadOut", new TypeReference("\ue951", null));
		put("loadReturn", new TypeReference(null, new ParameterizedTypeReference<PickList>() {
		}));
		put("loadVariance", new TypeReference("\ue907", null));
		put("loadVariances", new TypeReference(null, new ParameterizedTypeReference<List<SalesItemVariance>>() {
		}));
		put("orderConfirmation", new TypeReference("\ue911", new ParameterizedTypeReference<Billable>() {
		}));
		put("orderConfirmations", new TypeReference(null, new ParameterizedTypeReference<List<Billable>>() {
		}));
		put("payment", new TypeReference("\ue93d", null));
		put("payments", new TypeReference(null, new ParameterizedTypeReference<List<SalesItemVariance>>() {
		}));
		put("receivingReport", new TypeReference("\ue90c", null));
		put("remittanceVariance", new TypeReference("\ue906", null));
		put("remittanceVariances", new TypeReference(null, new ParameterizedTypeReference<List<SalesItemVariance>>() {
		}));
	}
}
