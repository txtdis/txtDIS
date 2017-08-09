package ph.txtdis.mgdc.gsm.util;

import org.springframework.core.ParameterizedTypeReference;
import ph.txtdis.dto.PickList;
import ph.txtdis.dto.Route;
import ph.txtdis.dto.User;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.util.AbstractTypeMap;
import ph.txtdis.util.TypeReference;

import java.util.List;

public class GsmServerTypeMapImpl //
	extends AbstractTypeMap {

	private static final long serialVersionUID = -5360838591923833592L;

	public GsmServerTypeMapImpl() {
		put("customer", new TypeReference("\ue92c", new ParameterizedTypeReference<Customer>() {
		}));
		put("customers", new TypeReference(null, new ParameterizedTypeReference<List<Customer>>() {
		}));
		put("driver", new TypeReference(null, new ParameterizedTypeReference<User>() {
		}));
		put("helper", new TypeReference(null, new ParameterizedTypeReference<User>() {
		}));
		put("item", new TypeReference("\ue90a", new ParameterizedTypeReference<Item>() {
		}));
		put("items", new TypeReference(null, new ParameterizedTypeReference<List<Item>>() {
		}));
		put("pickList", new TypeReference("\ue911", new ParameterizedTypeReference<PickList>() {
		}));
		put("pickLists", new TypeReference(null, new ParameterizedTypeReference<List<PickList>>() {
		}));
		put("route", new TypeReference("\ue936", new ParameterizedTypeReference<Route>() {
		}));
		put("routes", new TypeReference(null, new ParameterizedTypeReference<List<Route>>() {
		}));
	}
}
