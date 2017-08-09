package ph.txtdis.util;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import lombok.EqualsAndHashCode;
import ph.txtdis.dto.Authority;
import ph.txtdis.dto.Inventory;
import ph.txtdis.dto.Remittance;
import ph.txtdis.dto.Script;
import ph.txtdis.dto.StockTake;
import ph.txtdis.dto.StockTakeVariance;
import ph.txtdis.dto.Style;
import ph.txtdis.dto.Truck;
import ph.txtdis.dto.User;
import ph.txtdis.dto.Warehouse;

public abstract class AbstractTypeMap //
	extends LinkedHashMap<String, TypeReference> //
	implements TypeMap {

	protected static final long serialVersionUID = -6805898257725092194L;

	public AbstractTypeMap() {
		put("accept", new TypeReference("\ue95f", null));
		put("back", new TypeReference("\ue902", null));
		put("badOrderReceipt", new TypeReference("\ue94c", null));
		put("checkSearch", new TypeReference("\ue915", null));
		put("cheque", new TypeReference("\ue916", null));
		put("customerList", new TypeReference("\ue92c", null));
		put("dataDump", new TypeReference("\ue91d", null));
		put("dateRange", new TypeReference("\ue913", null));
		put("deactivate", new TypeReference("\ue921", null));
		put("decision", new TypeReference("\ue92f", null));
		put("deposit", new TypeReference("\ue901", null));
		put("disposal", new TypeReference("\ue91e", null));
		put("download", new TypeReference("\ue960", null));
		put("edit", new TypeReference("\ue93b", null));
		put("excel", new TypeReference("\ue920", null));
		put("inventory", new TypeReference("\ue963", new ParameterizedTypeReference<Inventory>() {
		}));
		put("inventories", new TypeReference(null, new ParameterizedTypeReference<List<Inventory>>() {
		}));
		put("list", new TypeReference("\ue931", null));
		put("mail", new TypeReference("\ue934", null));
		put("next", new TypeReference("\ue938", null));
		put("new", new TypeReference("\ue91f", null));
		put("openByDate", new TypeReference("\ue913", null));
		put("openByNo", new TypeReference("\ue924", null));
		put("override", new TypeReference("\ue91c", null));
		put("print", new TypeReference("\ue940", null));
		put("remittance", new TypeReference("\ue918", new ParameterizedTypeReference<Remittance>() {
		}));
		put("remittances", new TypeReference(null, new ParameterizedTypeReference<List<Remittance>>() {
		}));
		put("reject", new TypeReference("\ue962", null));
		put("returnReceipt", new TypeReference("\ue955", null));
		put("revenueReport", new TypeReference("\ue93d", null));
		put("role", new TypeReference("\ue943", new ParameterizedTypeReference<Authority>() {
		}));
		put("roles", new TypeReference(null, new ParameterizedTypeReference<List<Authority>>() {
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
		put("truck", new TypeReference("\ue950", new ParameterizedTypeReference<Truck>() {
		}));
		put("trucks", new TypeReference(null, new ParameterizedTypeReference<List<Truck>>() {
		}));
		put("upload", new TypeReference("\ue961", null));
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
