package ph.txtdis.dyvek.info;

import static ph.txtdis.type.ModuleType.DELIVERY_TO_PURCHASE_ORDER;
import static ph.txtdis.type.ModuleType.DELIVERY_TO_SALES_ORDER;
import static ph.txtdis.type.ModuleType.PURCHASE_ORDER;
import static ph.txtdis.type.ModuleType.SALES_BILLING;
import static ph.txtdis.type.ModuleType.valueOf;

import java.util.List;

import ph.txtdis.info.Information;
import ph.txtdis.type.ModuleType;

public class ToDoReminder
	extends Information {

	private static final long serialVersionUID = -1793071904017277L;

	private static final String A_DELIVERY_HAS_NOT_BEEN = "A delivery has not been ";

	public ToDoReminder(List<String> toDo) {
		super(reminder(toDo));
	}

	private static String reminder(List<String> toDo) {
		ModuleType m = valueOf(toDo.get(0));
		if (m == PURCHASE_ORDER)
			return "A not-fully-served Supplier P/O\nis expiring soon";
		if (m == DELIVERY_TO_PURCHASE_ORDER)
			return unassignedDelivery("Supplier");
		if (m == DELIVERY_TO_SALES_ORDER)
			return unassignedDelivery("Customer");
		if (m == SALES_BILLING)
			return unbilledDelivery("Customer");
		return "";
	}

	private static String unassignedDelivery(String customer) {
		return A_DELIVERY_HAS_NOT_BEEN + "assigned\nto a " + customer + " P/O";
	}

	private static String unbilledDelivery(String string) {
		return A_DELIVERY_HAS_NOT_BEEN + "billed";
	}
}
