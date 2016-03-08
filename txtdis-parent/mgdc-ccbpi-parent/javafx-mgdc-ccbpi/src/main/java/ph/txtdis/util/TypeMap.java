package ph.txtdis.util;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import ph.txtdis.app.Startable;
import ph.txtdis.dto.Authority;
import ph.txtdis.dto.Bank;
import ph.txtdis.dto.Billing;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.Item;
import ph.txtdis.dto.Payment;
import ph.txtdis.dto.SalesRevenue;
import ph.txtdis.dto.Style;
import ph.txtdis.dto.User;
import ph.txtdis.util.TypeMap.Type;

@Component
public class TypeMap extends LinkedHashMap<String, Type> {

	static final class Type {

		private String icon;

		private ParameterizedTypeReference<?> type;

		public Type(String icon, ParameterizedTypeReference<?> type) {
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

	private static final long serialVersionUID = -1782679034968493608L;

	public TypeMap() {
		put("back", new Type("\ue803", null));
		put("bank", new Type("\ue81f", new ParameterizedTypeReference<Bank>() {
		}));
		put("banks", new Type(null, new ParameterizedTypeReference<List<Bank>>() {
		}));
		put("booking", new Type("\ue829", new ParameterizedTypeReference<Billing>() {
		}));
		put("bookings", new Type(null, new ParameterizedTypeReference<List<Billing>>() {
		}));
		put("customer", new Type("\ue808", new ParameterizedTypeReference<Customer>() {
		}));
		put("customers", new Type(null, new ParameterizedTypeReference<List<Customer>>() {
		}));
		put("customerList", new Type("\ue809", null));
		put("dataDump", new Type("\ue821", null));
		put("dateRange", new Type("\ue807", null));
		put("deactivate", new Type("\ue903", null));
		put("edit", new Type("\ue80d", null));
		put("excel", new Type("\ue810", null));
		put("import", new Type("\ue821", new ParameterizedTypeReference<Billing>() {
		}));
		put("imports", new Type(null, new ParameterizedTypeReference<List<Billing>>() {
		}));
		put("invoice", new Type("\ue817", new ParameterizedTypeReference<Billing>() {
		}));
		put("invoices", new Type(null, new ParameterizedTypeReference<List<Billing>>() {
		}));
		put("item", new Type("\ue819", new ParameterizedTypeReference<Item>() {
		}));
		put("items", new Type(null, new ParameterizedTypeReference<List<Item>>() {
		}));
		put("list", new Type("\ue906", null));
		put("mail", new Type("\ue842", null));
		put("next", new Type("\ue81a", null));
		put("new", new Type("\ue800", null));
		put("openByDate", new Type("\ue807", null));
		put("openByNo", new Type("\ue81b", null));
		put("pickList", new Type("\ue805", new ParameterizedTypeReference<Billing>() {
		}));
		put("pickLists", new Type(null, new ParameterizedTypeReference<List<Billing>>() {
		}));
		put("print", new Type("\ue81c", null));
		put("salesOrder", new Type("\ue834", new ParameterizedTypeReference<Billing>() {
		}));
		put("salesOrders", new Type(null, new ParameterizedTypeReference<List<Billing>>() {
		}));
		put("salesReturn", new Type("\ue81e", new ParameterizedTypeReference<Billing>() {
		}));
		put("salesRevenue", new Type("\ue820", null));
		put("salesRevenues", new Type(null, new ParameterizedTypeReference<List<SalesRevenue>>() {
		}));
		put("receivingReport", new Type("\ue832", new ParameterizedTypeReference<Billing>() {
		}));
		put("receivingReports", new Type(null, new ParameterizedTypeReference<List<Billing>>() {
		}));
		put("reconciliation", new Type("\uf24e", new ParameterizedTypeReference<Billing>() {
		}));
		put("reconciliations", new Type(null, new ParameterizedTypeReference<List<Billing>>() {
		}));
		put("remittance", new Type("\ue837", new ParameterizedTypeReference<Payment>() {
		}));
		put("remittances", new Type(null, new ParameterizedTypeReference<List<Payment>>() {
		}));
		put("role", new Type("\ue912", new ParameterizedTypeReference<Authority>() {
		}));
		put("roles", new Type(null, new ParameterizedTypeReference<List<Authority>>() {
		}));
		put("save", new Type("\ue823", null));
		put("search", new Type("\ue824", null));
		put("style", new Type("\ue825", new ParameterizedTypeReference<Style>() {
		}));
		put("styles", new Type(null, new ParameterizedTypeReference<List<Style>>() {
		}));
		put("user", new Type("\ue82d", new ParameterizedTypeReference<User>() {
		}));
		put("users", new Type(null, new ParameterizedTypeReference<List<User>>() {
		}));
	}

	public String icon(Startable a) {
		return icon(TextUtils.toName(a));
	}

	public String icon(String name) {
		Type type = get(name);
		return type == null ? null : type.getIcon();
	}

	public ParameterizedTypeReference<?> type(String path) {
		Type type = get(path);
		return type == null ? null : type.getType();
	}
}
