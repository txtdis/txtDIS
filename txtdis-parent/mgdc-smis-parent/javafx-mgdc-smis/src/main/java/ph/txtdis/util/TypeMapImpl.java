package ph.txtdis.util;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import ph.txtdis.app.Startable;
import ph.txtdis.dto.Account;
import ph.txtdis.dto.AgingReceivableReport;
import ph.txtdis.dto.Authority;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Channel;
import ph.txtdis.dto.CreditNote;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.CustomerReceivableReport;
import ph.txtdis.dto.Holiday;
import ph.txtdis.dto.Inventory;
import ph.txtdis.dto.InvoiceBooklet;
import ph.txtdis.dto.Item;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.ItemTree;
import ph.txtdis.dto.Location;
import ph.txtdis.dto.Payment;
import ph.txtdis.dto.PickList;
import ph.txtdis.dto.PricingType;
import ph.txtdis.dto.Route;
import ph.txtdis.dto.SalesRevenue;
import ph.txtdis.dto.SalesVolume;
import ph.txtdis.dto.SalesforceAccount;
import ph.txtdis.dto.SalesforceSalesInfo;
import ph.txtdis.dto.Script;
import ph.txtdis.dto.StockTake;
import ph.txtdis.dto.Style;
import ph.txtdis.dto.Truck;
import ph.txtdis.dto.User;
import ph.txtdis.dto.Vat;
import ph.txtdis.dto.Warehouse;

@Component
public class TypeMapImpl extends LinkedHashMap<String, TypeReference> implements TypeMap {

	private static final long serialVersionUID = -1782679034968493608L;

	public TypeMapImpl() {
		put("accept", new TypeReference("\ue845", null));
		put("accounts", new TypeReference(null, new ParameterizedTypeReference<List<Account>>() {
		}));
		put("account", new TypeReference("\ue834", new ParameterizedTypeReference<Account>() {
		}));
		put("agingReceivable", new TypeReference("\ue802", new ParameterizedTypeReference<AgingReceivableReport>() {
		}));
		put("back", new TypeReference("\ue803", null));
		put("badOrder", new TypeReference("\ue80a", new ParameterizedTypeReference<Billable>() {
		}));
		put("billable", new TypeReference(null, new ParameterizedTypeReference<Billable>() {
		}));
		put("billables", new TypeReference(null, new ParameterizedTypeReference<List<Billable>>() {
		}));
		put("channel", new TypeReference("\ue808", new ParameterizedTypeReference<Channel>() {
		}));
		put("channels", new TypeReference(null, new ParameterizedTypeReference<List<Channel>>() {
		}));
		put("checkSearch", new TypeReference("\ue904", null));
		put("cheque", new TypeReference("\ue90f", null));
		put("creditNote", new TypeReference("\ue806", new ParameterizedTypeReference<CreditNote>() {
		}));
		put("creditNotes", new TypeReference(null, new ParameterizedTypeReference<List<CreditNote>>() {
		}));
		put("customer", new TypeReference("\ue809", new ParameterizedTypeReference<Customer>() {
		}));
		put("customers", new TypeReference(null, new ParameterizedTypeReference<List<Customer>>() {
		}));
		put("customerList", new TypeReference("\ue809", null));
		put("customerReceivable", new TypeReference("\ue802", new ParameterizedTypeReference<CustomerReceivableReport>() {
		}));
		put("dataDump", new TypeReference("\ue917", null));
		put("dateRange", new TypeReference("\ue807", null));
		put("deactivate", new TypeReference("\ue903", null));
		put("decision", new TypeReference("\ue900", null));
		put("deliveryReport", new TypeReference("\ue906", new ParameterizedTypeReference<Billable>() {
		}));
		put("deposit", new TypeReference("\ue913", null));
		put("disposal", new TypeReference("\ue90d", null));
		put("download", new TypeReference("\uf0ed", null));
		put("edit", new TypeReference("\ue80d", null));
		put("excel", new TypeReference("\ue810", null));
		put("holiday", new TypeReference("\ue914", new ParameterizedTypeReference<Holiday>() {
		}));
		put("holidays", new TypeReference(null, new ParameterizedTypeReference<List<Holiday>>() {
		}));
		put("inventory", new TypeReference("\ue814", new ParameterizedTypeReference<Inventory>() {
		}));
		put("inventories", new TypeReference(null, new ParameterizedTypeReference<List<Inventory>>() {
		}));
		put("invoice", new TypeReference("\ue817", null));
		put("invoiceBooklet", new TypeReference("\ue816", new ParameterizedTypeReference<InvoiceBooklet>() {
		}));
		put("invoiceBooklets", new TypeReference(null, new ParameterizedTypeReference<List<InvoiceBooklet>>() {
		}));
		put("item", new TypeReference("\ue819", new ParameterizedTypeReference<Item>() {
		}));
		put("items", new TypeReference(null, new ParameterizedTypeReference<List<Item>>() {
		}));
		put("itemFamily", new TypeReference("\ue836", new ParameterizedTypeReference<ItemFamily>() {
		}));
		put("itemFamilies", new TypeReference(null, new ParameterizedTypeReference<List<ItemFamily>>() {
		}));
		put("itemTree", new TypeReference("\ue852", new ParameterizedTypeReference<ItemTree>() {
		}));
		put("itemTrees", new TypeReference(null, new ParameterizedTypeReference<List<ItemTree>>() {
		}));
		put("list", new TypeReference("\ue906", null));
		put("locations", new TypeReference(null, new ParameterizedTypeReference<List<Location>>() {
		}));
		put("mail", new TypeReference("\ue842", null));
		put("next", new TypeReference("\ue81a", null));
		put("new", new TypeReference("\ue800", null));
		put("openByDate", new TypeReference("\ue807", null));
		put("openByNo", new TypeReference("\ue81b", null));
		put("override", new TypeReference("\ue83f", null));
		put("pickList", new TypeReference("\ue805", new ParameterizedTypeReference<PickList>() {
		}));
		put("pickLists", new TypeReference(null, new ParameterizedTypeReference<List<PickList>>() {
		}));
		put("pricingType", new TypeReference("\ue911", new ParameterizedTypeReference<PricingType>() {
		}));
		put("pricingTypes", new TypeReference(null, new ParameterizedTypeReference<List<PricingType>>() {
		}));
		put("print", new TypeReference("\ue81c", null));
		put("purchaseOrder", new TypeReference("\ue81d", new ParameterizedTypeReference<Billable>() {
		}));
		put("purchaseReceipt", new TypeReference("\ue90a", new ParameterizedTypeReference<Billable>() {
		}));
		put("remittance", new TypeReference("\ue837", new ParameterizedTypeReference<Payment>() {
		}));
		put("remittances", new TypeReference(null, new ParameterizedTypeReference<List<Payment>>() {
		}));
		put("reject", new TypeReference("\ue846", null));
		put("returnOrder", new TypeReference("\ue83c", new ParameterizedTypeReference<Billable>() {
		}));
		put("returnReceipt", new TypeReference("\ue90c", null));
		put("revenueReport", new TypeReference("\ue81f", null));
		put("role", new TypeReference("\ue912", new ParameterizedTypeReference<Authority>() {
		}));
		put("roles", new TypeReference(null, new ParameterizedTypeReference<List<Authority>>() {
		}));
		put("route", new TypeReference("\ue822", new ParameterizedTypeReference<Route>() {
		}));
		put("routes", new TypeReference(null, new ParameterizedTypeReference<List<Route>>() {
		}));
		put("salesforceAccounts", new TypeReference(null, new ParameterizedTypeReference<List<SalesforceAccount>>() {
		}));
		put("salesforceSalesInfos", new TypeReference(null, new ParameterizedTypeReference<List<SalesforceSalesInfo>>() {
		}));
		put("salesOrder", new TypeReference("\ue829", new ParameterizedTypeReference<Billable>() {
		}));
		put("salesOrders", new TypeReference(null, new ParameterizedTypeReference<List<Billable>>() {
		}));
		put("salesReturn", new TypeReference("\ue81e", new ParameterizedTypeReference<Billable>() {
		}));
		put("salesRevenue", new TypeReference("\ue820", null));
		put("salesRevenues", new TypeReference(null, new ParameterizedTypeReference<List<SalesRevenue>>() {
		}));
		put("salesVolume", new TypeReference("\ue80b", null));
		put("salesVolumes", new TypeReference(null, new ParameterizedTypeReference<List<SalesVolume>>() {
		}));
		put("save", new TypeReference("\ue823", null));
		put("script", new TypeReference(null, new ParameterizedTypeReference<Script>() {
		}));
		put("scripts", new TypeReference(null, new ParameterizedTypeReference<List<Script>>() {
		}));
		put("search", new TypeReference("\ue824", null));
		put("settings", new TypeReference("\ue801", null));
		put("stockTake", new TypeReference("\ue84e", new ParameterizedTypeReference<StockTake>() {
		}));
		// TODO
		put("stockTakeReconciliation", new TypeReference("\ue907", new ParameterizedTypeReference<StockTake>() {
		}));
		put("style", new TypeReference("\ue825", new ParameterizedTypeReference<Style>() {
		}));
		put("styles", new TypeReference(null, new ParameterizedTypeReference<List<Style>>() {
		}));
		put("sync", new TypeReference(null, new ParameterizedTypeReference<String>() {
		}));
		put("toSalesforce", new TypeReference("\ue918", null));
		// TODO
		put("transfer", new TypeReference("\ue833", null));
		put("truck", new TypeReference("\ue838", new ParameterizedTypeReference<Truck>() {
		}));
		put("trucks", new TypeReference(null, new ParameterizedTypeReference<List<Truck>>() {
		}));
		put("upload", new TypeReference("\uf0ee", null));
		put("user", new TypeReference("\ue82d", new ParameterizedTypeReference<User>() {
		}));
		put("users", new TypeReference(null, new ParameterizedTypeReference<List<User>>() {
		}));
		put("vat", new TypeReference("\ue844", new ParameterizedTypeReference<Vat>() {
		}));
		put("vats", new TypeReference(null, new ParameterizedTypeReference<List<Vat>>() {
		}));
		put("warehouse", new TypeReference("\ue830", new ParameterizedTypeReference<Warehouse>() {
		}));
		put("warehouses", new TypeReference(null, new ParameterizedTypeReference<List<Warehouse>>() {
		}));
	}

	@Override
	public String icon(Startable a) {
		return icon(NameUtils.toName(a));
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
