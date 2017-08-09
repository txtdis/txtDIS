package ph.txtdis.mgdc.util;

import org.springframework.core.ParameterizedTypeReference;
import ph.txtdis.dto.*;
import ph.txtdis.util.AbstractClientTypeMap;
import ph.txtdis.util.TypeReference;

import java.util.List;

public abstract class AbstractMgdcClientTypeMap //
	extends AbstractClientTypeMap {

	private static final long serialVersionUID = -1782679034968493608L;

	public AbstractMgdcClientTypeMap() {
		put("account", new TypeReference("\ue957", new ParameterizedTypeReference<Account>() {
		}));
		put("accounts", new TypeReference(null, new ParameterizedTypeReference<List<Account>>() {
		}));
		put("badOrderReceipt", new TypeReference("\ue94c", null));
		put("badRma", new TypeReference("\ue94b", new ParameterizedTypeReference<Billable>() {
		}));
		put("billable", new TypeReference("\ue914", new ParameterizedTypeReference<Billable>() {
		}));
		put("billables", new TypeReference(null, new ParameterizedTypeReference<List<Billable>>() {
		}));
		put("boms", new TypeReference(null, new ParameterizedTypeReference<List<Bom>>() {
		}));
		put("holiday", new TypeReference("\ue922", new ParameterizedTypeReference<Holiday>() {
		}));
		put("holidays", new TypeReference(null, new ParameterizedTypeReference<List<Holiday>>() {
		}));
		put("inventory", new TypeReference("\ue963", new ParameterizedTypeReference<Inventory>() {
		}));
		put("inventories", new TypeReference(null, new ParameterizedTypeReference<List<Inventory>>() {
		}));
		put("itemFamily", new TypeReference("\ue94e", new ParameterizedTypeReference<ItemFamily>() {
		}));
		put("itemFamilies", new TypeReference(null, new ParameterizedTypeReference<List<ItemFamily>>() {
		}));
		put("location", new TypeReference(null, new ParameterizedTypeReference<Location>() {
		}));
		put("locations", new TypeReference(null, new ParameterizedTypeReference<List<Location>>() {
		}));
		put("pickList", new TypeReference("\ue911", new ParameterizedTypeReference<PickList>() {
		}));
		put("pickLists", new TypeReference(null, new ParameterizedTypeReference<List<PickList>>() {
		}));
		put("pricingType", new TypeReference("\ue93f", new ParameterizedTypeReference<PricingType>() {
		}));
		put("pricingTypes", new TypeReference(null, new ParameterizedTypeReference<List<PricingType>>() {
		}));
		put("purchaseReceipt", new TypeReference("\ue929", new ParameterizedTypeReference<Billable>() {
		}));
		put("purchaseReceipts", new TypeReference(null, new ParameterizedTypeReference<List<Billable>>() {
		}));
		put("receivingReport", new TypeReference("\ue90c", new ParameterizedTypeReference<Billable>() {
		}));
		put("rma", new TypeReference(null, new ParameterizedTypeReference<Billable>() {
		}));
		put("route", new TypeReference("\ue936", new ParameterizedTypeReference<Route>() {
		}));
		put("routes", new TypeReference(null, new ParameterizedTypeReference<List<Route>>() {
		}));
		put("stockTake", new TypeReference("\ue90f", new ParameterizedTypeReference<StockTake>() {
		}));
		put("stockTakeVariance", new TypeReference("\ue905", new ParameterizedTypeReference<StockTakeVariance>() {
		}));
		put("stockTakeVariances", new TypeReference(null, new ParameterizedTypeReference<List<StockTakeVariance>>() {
		}));
	}
}
