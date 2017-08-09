package ph.txtdis.mgdc.ccbpi.util;

import java.util.List;

import lombok.EqualsAndHashCode;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.PickList;
import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.mgdc.ccbpi.dto.Channel;
import ph.txtdis.mgdc.ccbpi.dto.Item;
import ph.txtdis.mgdc.util.AbstractMgdcClientTypeMap;
import ph.txtdis.util.TypeReference;

@Component("typeMap")
public class CokeTypeMapImpl //
	extends AbstractMgdcClientTypeMap {

	private static final long serialVersionUID = -5360838591923833592L;

	public CokeTypeMapImpl() {
		put("bookingVariance", new TypeReference("\ue904", null));
		put("bookingVariances", new TypeReference(null, new ParameterizedTypeReference<List<SalesItemVariance>>() {
		}));
		put("blanket", new TypeReference("\ue910", new ParameterizedTypeReference<Billable>() {
		}));
		put("blanketBalance", new TypeReference("\ue910", null));
		put("blanketBalances", new TypeReference(null, new ParameterizedTypeReference<List<SalesItemVariance>>() {
		}));
		put("channel", new TypeReference("\ue948", new ParameterizedTypeReference<Channel>() {
		}));
		put("channels", new TypeReference(null, new ParameterizedTypeReference<List<Channel>>() {
		}));
		put("deliveryList", new TypeReference("\ue931", new ParameterizedTypeReference<Billable>() {
		}));
		put("deliveryReturn", new TypeReference("\ue954", null));
		put("deliveryVariances", new TypeReference(null, new ParameterizedTypeReference<List<SalesItemVariance>>() {
		}));
		put("item", new TypeReference("\ue90a", new ParameterizedTypeReference<Item>() {
		}));
		put("items", new TypeReference(null, new ParameterizedTypeReference<List<Item>>() {
		}));
		put("loadIn", new TypeReference("\ue952", null));
		put("loadManifest", new TypeReference("\ue90b", new ParameterizedTypeReference<Billable>() {
		}));
		put("loadOut", new TypeReference("\ue951", null));
		put("loadReturn", new TypeReference("\ue952", new ParameterizedTypeReference<PickList>() {
		}));
		put("loadVariance", new TypeReference("\ue907", null));
		put("loadVariances", new TypeReference(null, new ParameterizedTypeReference<List<SalesItemVariance>>() {
		}));
		put("orderConfirmation", new TypeReference("\ue911", new ParameterizedTypeReference<Billable>() {
		}));
		put("orderConfirmations", new TypeReference(null, new ParameterizedTypeReference<List<Billable>>() {
		}));
		put("orderReturn", new TypeReference("\ue90c", new ParameterizedTypeReference<Billable>() {
		}));
		put("payment", new TypeReference("\ue93d", null));
		put("payments", new TypeReference(null, new ParameterizedTypeReference<List<SalesItemVariance>>() {
		}));
		put("remittanceVariance", new TypeReference("\ue906", null));
		put("remittanceVariances", new TypeReference(null, new ParameterizedTypeReference<List<SalesItemVariance>>() {
		}));
		put("return", new TypeReference("\ue942", null));
	}
}
