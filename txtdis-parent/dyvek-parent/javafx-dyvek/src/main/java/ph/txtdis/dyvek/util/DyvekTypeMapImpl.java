package ph.txtdis.dyvek.util;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.AgingReceivableReport;
import ph.txtdis.dto.Remittance;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.model.CashAdvance;
import ph.txtdis.dyvek.model.Customer;
import ph.txtdis.dyvek.model.Item;
import ph.txtdis.util.AbstractClientTypeMap;
import ph.txtdis.util.TypeReference;

@Component("typeMap")
public class DyvekTypeMapImpl //
		extends AbstractClientTypeMap {

	private static final long serialVersionUID = 1414651091475898263L;

	public DyvekTypeMapImpl() {
		put("agingPayable", new TypeReference("\ue91b", new ParameterizedTypeReference<AgingReceivableReport>() {
		}));
		put("agingReceivable", new TypeReference("\ue912", new ParameterizedTypeReference<AgingReceivableReport>() {
		}));
		put("bank", new TypeReference("\ue901", new ParameterizedTypeReference<Customer>() {
		}));
		put("banks", new TypeReference(null, new ParameterizedTypeReference<List<Customer>>() {
		}));
		put("bankRecon", new TypeReference("\ue906", new ParameterizedTypeReference<Billable>() {
		}));
		put("billable", new TypeReference("\ue914", new ParameterizedTypeReference<Billable>() {
		}));
		put("billables", new TypeReference(null, new ParameterizedTypeReference<List<Billable>>() {
		}));
		put("cashAdvance", new TypeReference("\ue95a", new ParameterizedTypeReference<CashAdvance>() {
		}));
		put("cashAdvances", new TypeReference(null, new ParameterizedTypeReference<List<CashAdvance>>() {
		}));
		put("cashAdvanceLiquidation", new TypeReference("\ue95a", null));
		put("cashAdvanceLiquidations", new TypeReference(null, new ParameterizedTypeReference<List<RemittanceDetail>>() {
		}));
		put("clientBill", new TypeReference("\ue914", new ParameterizedTypeReference<Billable>() {
		}));
		put("clientBills", new TypeReference(null, new ParameterizedTypeReference<List<Billable>>() {
		}));
		put("clientBillAssignment", new TypeReference("\ue909", new ParameterizedTypeReference<Billable>() {
		}));
		put("clientBillAssignments", new TypeReference(null, new ParameterizedTypeReference<List<Billable>>() {
		}));
		put("collection", new TypeReference("\ue919", new ParameterizedTypeReference<Remittance>() {
		}));
		put("commission", new TypeReference("\ue91a", new ParameterizedTypeReference<Billable>() {
		}));
		put("customer", new TypeReference("\ue92c", new ParameterizedTypeReference<Customer>() {
		}));
		put("customers", new TypeReference(null, new ParameterizedTypeReference<List<Customer>>() {
		}));
		put("deliveryReport", new TypeReference("\ue929", new ParameterizedTypeReference<Billable>() {
		}));
		put("deliveryReports", new TypeReference(null, new ParameterizedTypeReference<List<Billable>>() {
		}));
		put("employee", new TypeReference("\ue943", new ParameterizedTypeReference<Billable>() {
		}));
		put("expense", new TypeReference("\ue918", new ParameterizedTypeReference<Billable>() {
		}));
		put("invoice", new TypeReference("\ue914", new ParameterizedTypeReference<Billable>() {
		}));
		put("intray", new TypeReference("\ue92e", null));
		put("item", new TypeReference("\ue90a", new ParameterizedTypeReference<Item>() {
		}));
		put("items", new TypeReference(null, new ParameterizedTypeReference<List<Item>>() {
		}));
		put("payment", new TypeReference("\ue916", new ParameterizedTypeReference<Remittance>() {
		}));
		put("purchaseOrder", new TypeReference("\ue928", new ParameterizedTypeReference<Billable>() {
		}));
		put("purchaseOrders", new TypeReference(null, new ParameterizedTypeReference<List<Billable>>() {
		}));
		put("remoteStock", new TypeReference("\ue95b", new ParameterizedTypeReference<Billable>() {
		}));
		put("salesOrder", new TypeReference("\ue90b", new ParameterizedTypeReference<Billable>() {
		}));
		put("salesOrders", new TypeReference(null, new ParameterizedTypeReference<List<Billable>>() {
		}));
		put("supplier", new TypeReference("\ue948", new ParameterizedTypeReference<Customer>() {
		}));
		put("suppliers", new TypeReference(null, new ParameterizedTypeReference<List<Customer>>() {
		}));
		put("trading", new TypeReference("\ue954", null));
		put("truckClient", new TypeReference("\ue957", null));
		put("truckLog", new TypeReference("\ue953", new ParameterizedTypeReference<Billable>() {
		}));
		put("truckLogs", new TypeReference(null, new ParameterizedTypeReference<List<Billable>>() {
		}));
		put("vendorBill", new TypeReference("\ue916", new ParameterizedTypeReference<Billable>() {
		}));
		put("vendorBills", new TypeReference(null, new ParameterizedTypeReference<List<Billable>>() {
		}));
	}
}
