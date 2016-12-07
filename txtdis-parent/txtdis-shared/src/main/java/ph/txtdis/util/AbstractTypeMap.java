package ph.txtdis.util;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Bom;
import ph.txtdis.dto.Channel;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.Item;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.Location;
import ph.txtdis.dto.PickList;
import ph.txtdis.dto.Remittance;
import ph.txtdis.dto.Route;
import ph.txtdis.dto.Truck;
import ph.txtdis.dto.User;
import ph.txtdis.dto.Warehouse;

public abstract class AbstractTypeMap extends LinkedHashMap<String, TypeReference> implements TypeMap {

	private static final long serialVersionUID = -6805898257725092194L;

	public AbstractTypeMap() {
		put("billable", new TypeReference("\ue914", new ParameterizedTypeReference<Billable>() {
		}));
		put("billables", new TypeReference(null, new ParameterizedTypeReference<List<Billable>>() {
		}));
		put("boms", new TypeReference(null, new ParameterizedTypeReference<List<Bom>>() {
		}));
		put("channel", new TypeReference("\ue948", new ParameterizedTypeReference<Channel>() {
		}));
		put("channels", new TypeReference(null, new ParameterizedTypeReference<List<Channel>>() {
		}));
		put("customer", new TypeReference("\ue92c", new ParameterizedTypeReference<Customer>() {
		}));
		put("customers", new TypeReference(null, new ParameterizedTypeReference<List<Customer>>() {
		}));
		put("item", new TypeReference("\ue90a", new ParameterizedTypeReference<Item>() {
		}));
		put("items", new TypeReference(null, new ParameterizedTypeReference<List<Item>>() {
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
		put("remittance", new TypeReference("\ue918", new ParameterizedTypeReference<Remittance>() {
		}));
		put("remittances", new TypeReference(null, new ParameterizedTypeReference<List<Remittance>>() {
		}));
		put("route", new TypeReference("\ue936", new ParameterizedTypeReference<Route>() {
		}));
		put("routes", new TypeReference(null, new ParameterizedTypeReference<List<Route>>() {
		}));
		put("truck", new TypeReference("\ue950", new ParameterizedTypeReference<Truck>() {
		}));
		put("trucks", new TypeReference(null, new ParameterizedTypeReference<List<Truck>>() {
		}));
		put("user", new TypeReference("\ue939", new ParameterizedTypeReference<User>() {
		}));
		put("users", new TypeReference(null, new ParameterizedTypeReference<List<User>>() {
		}));
		put("warehouse", new TypeReference("\ue95b", new ParameterizedTypeReference<Warehouse>() {
		}));
		put("warehouses", new TypeReference(null, new ParameterizedTypeReference<List<Warehouse>>() {
		}));
	}

	@Override
	public ParameterizedTypeReference<?> type(String path) {
		TypeReference type = get(path);
		return type == null ? null : type.getType();
	}
}
