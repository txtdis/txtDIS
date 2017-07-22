package ph.txtdis.mgdc.gsm.util;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.CreditNote;
import ph.txtdis.dto.Holiday;
import ph.txtdis.dto.ItemTree;
import ph.txtdis.dto.SalesRevenue;
import ph.txtdis.dto.SalesVolume;
import ph.txtdis.mgdc.gsm.dto.Channel;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.mgdc.gsm.dto.Vat;
import ph.txtdis.mgdc.util.AbstractMgdcClientTypeMap;
import ph.txtdis.util.TypeReference;

@Component("typeMap")
public class GsmTypeMapImpl //
		extends AbstractMgdcClientTypeMap {

	private static final long serialVersionUID = -1782679034968493608L;

	public GsmTypeMapImpl() {
		put("channel", new TypeReference("\ue948", new ParameterizedTypeReference<Channel>() {
		}));
		put("channels", new TypeReference(null, new ParameterizedTypeReference<List<Channel>>() {
		}));
		put("creditNote", new TypeReference("\ue93e", new ParameterizedTypeReference<CreditNote>() {
		}));
		put("creditNotes", new TypeReference(null, new ParameterizedTypeReference<List<CreditNote>>() {
		}));
		put("customer", new TypeReference("\ue92c", new ParameterizedTypeReference<Customer>() {
		}));
		put("customers", new TypeReference(null, new ParameterizedTypeReference<List<Customer>>() {
		}));
		put("deliveryReport", new TypeReference("\ue931", new ParameterizedTypeReference<Billable>() {
		}));
		put("goodRma", new TypeReference("\ue954", new ParameterizedTypeReference<Billable>() {
		}));
		put("holiday", new TypeReference("\ue922", new ParameterizedTypeReference<Holiday>() {
		}));
		put("holidays", new TypeReference(null, new ParameterizedTypeReference<List<Holiday>>() {
		}));
		put("invoice", new TypeReference("\ue914", new ParameterizedTypeReference<Billable>() {
		}));
		put("item", new TypeReference("\ue90a", new ParameterizedTypeReference<Item>() {
		}));
		put("items", new TypeReference(null, new ParameterizedTypeReference<List<Item>>() {
		}));
		put("itemTree", new TypeReference("\ue94d", new ParameterizedTypeReference<ItemTree>() {
		}));
		put("itemTrees", new TypeReference(null, new ParameterizedTypeReference<List<ItemTree>>() {
		}));
		put("loadOrder", new TypeReference("\ue951", new ParameterizedTypeReference<Billable>() {
		}));
		put("loadOrders", new TypeReference(null, new ParameterizedTypeReference<List<Billable>>() {
		}));
		put("payment", new TypeReference("\ue919", null));
		put("reactivate", new TypeReference("\ue965", null));
		put("salesOrder", new TypeReference("\ue90b", new ParameterizedTypeReference<Billable>() {
		}));
		put("salesOrders", new TypeReference(null, new ParameterizedTypeReference<List<Billable>>() {
		}));
		put("salesRevenue", new TypeReference("\ue908", null));
		put("salesRevenues", new TypeReference(null, new ParameterizedTypeReference<List<SalesRevenue>>() {
		}));
		put("salesVolume", new TypeReference("\ue949", null));
		put("salesVolumes", new TypeReference(null, new ParameterizedTypeReference<List<SalesVolume>>() {
		}));
		put("siv", new TypeReference("\ue90d", new ParameterizedTypeReference<Billable>() {
		}));
		put("vat", new TypeReference("\ue95e", new ParameterizedTypeReference<Vat>() {
		}));
		put("vats", new TypeReference(null, new ParameterizedTypeReference<List<Vat>>() {
		}));
	}
}
