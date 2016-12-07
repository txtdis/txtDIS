package ph.txtdis.util;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import ph.txtdis.dto.Account;
import ph.txtdis.dto.Authority;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Holiday;
import ph.txtdis.dto.Inventory;
import ph.txtdis.dto.PricingType;
import ph.txtdis.dto.Script;
import ph.txtdis.dto.StockTake;
import ph.txtdis.dto.StockTakeVariance;
import ph.txtdis.dto.Style;
import ph.txtdis.dto.Vat;

public abstract class AbstractClientTypeMap extends AbstractTypeMap implements ClientTypeMap {

	private static final long serialVersionUID = -1782679034968493608L;

	public AbstractClientTypeMap() {
		put("accept", new TypeReference("\ue95f", null));
		put("account", new TypeReference("\ue957", new ParameterizedTypeReference<Account>() {
		}));
		put("accounts", new TypeReference(null, new ParameterizedTypeReference<List<Account>>() {
		}));
		put("back", new TypeReference("\ue902", null));
		put("badRma", new TypeReference("\ue94b", new ParameterizedTypeReference<Billable>() {
		}));
		put("badOrderReceipt", new TypeReference("\ue94c", null));
		put("checkSearch", new TypeReference("\ue915", null));
		put("cheque", new TypeReference("\ue916", null));
		put("customerList", new TypeReference("\ue92c", null));
		put("dataDump", new TypeReference("\ue91d", null));
		put("dateRange", new TypeReference("\ue913", null));
		put("deactivate", new TypeReference("\ue921", null));
		put("decision", new TypeReference("\ue92f", null));
		put("deliveryReport", new TypeReference("\ue931", new ParameterizedTypeReference<Billable>() {
		}));
		put("deposit", new TypeReference("\ue901", null));
		put("disposal", new TypeReference("\ue91e", null));
		put("download", new TypeReference("\ue960", null));
		put("edit", new TypeReference("\ue93b", null));
		put("excel", new TypeReference("\ue920", null));
		put("goodRma", new TypeReference("\ue954", new ParameterizedTypeReference<Billable>() {
		}));
		put("holiday", new TypeReference("\ue922", new ParameterizedTypeReference<Holiday>() {
		}));
		put("holidays", new TypeReference(null, new ParameterizedTypeReference<List<Holiday>>() {
		}));
		put("inventory", new TypeReference("\ue963", new ParameterizedTypeReference<Inventory>() {
		}));
		put("inventories", new TypeReference(null, new ParameterizedTypeReference<List<Inventory>>() {
		}));
		put("invoice", new TypeReference("\ue914", null));
		put("list", new TypeReference("\ue931", null));
		put("mail", new TypeReference("\ue934", null));
		put("next", new TypeReference("\ue938", null));
		put("new", new TypeReference("\ue91f", null));
		put("openByDate", new TypeReference("\ue913", null));
		put("openByNo", new TypeReference("\ue924", null));
		put("override", new TypeReference("\ue91c", null));
		put("pricingType", new TypeReference("\ue93f", new ParameterizedTypeReference<PricingType>() {
		}));
		put("pricingTypes", new TypeReference(null, new ParameterizedTypeReference<List<PricingType>>() {
		}));
		put("purchaseOrder", new TypeReference("\ue928", new ParameterizedTypeReference<Billable>() {
		}));
		put("purchaseReceipt", new TypeReference("\ue929", new ParameterizedTypeReference<Billable>() {
		}));
		put("print", new TypeReference("\ue940", null));
		put("reject", new TypeReference("\ue962", null));
		put("receivingReport", new TypeReference("\ue90c", new ParameterizedTypeReference<Billable>() {
		}));
		put("returnReceipt", new TypeReference("\ue955", null));
		put("revenueReport", new TypeReference("\ue93d", null));
		put("role", new TypeReference("\ue943", new ParameterizedTypeReference<Authority>() {
		}));
		put("roles", new TypeReference(null, new ParameterizedTypeReference<List<Authority>>() {
		}));
		put("salesOrder", new TypeReference("\ue90b", new ParameterizedTypeReference<Billable>() {
		}));
		put("salesOrders", new TypeReference(null, new ParameterizedTypeReference<List<Billable>>() {
		}));
		put("salesRevenue", new TypeReference("\ue908", null));
		put("salesVolume", new TypeReference("\ue949", null));
		put("save", new TypeReference("\ue923", null));
		put("script", new TypeReference(null, new ParameterizedTypeReference<Script>() {
		}));
		put("scripts", new TypeReference(null, new ParameterizedTypeReference<List<Script>>() {
		}));
		put("search", new TypeReference("\ue933", null));
		put("settings", new TypeReference("\ue92a", null));
		put("stockTake", new TypeReference("\ue90f", new ParameterizedTypeReference<StockTake>() {
		}));
		put("stockTakeVariance", new TypeReference("\ue905", new ParameterizedTypeReference<StockTakeVariance>() {
		}));
		put("stockTakeVariances", new TypeReference(null, new ParameterizedTypeReference<List<StockTakeVariance>>() {
		}));
		put("style", new TypeReference("\ue956", new ParameterizedTypeReference<Style>() {
		}));
		put("styles", new TypeReference(null, new ParameterizedTypeReference<List<Style>>() {
		}));
		put("sync", new TypeReference(null, new ParameterizedTypeReference<String>() {
		}));
		put("toSalesforce", new TypeReference("\ue926", null));
		put("upload", new TypeReference("\ue961", null));
		put("vat", new TypeReference("\ue95e", new ParameterizedTypeReference<Vat>() {
		}));
		put("vats", new TypeReference(null, new ParameterizedTypeReference<List<Vat>>() {
		}));
	}

	@Override
	public String icon(String name) {
		TypeReference type = get(name);
		return type == null ? null : type.getIcon();
	}

	@Override
	public ParameterizedTypeReference<?> type(String path) {
		TypeReference type = get(path);
		return type == null ? null : type.getType();
	}
}
