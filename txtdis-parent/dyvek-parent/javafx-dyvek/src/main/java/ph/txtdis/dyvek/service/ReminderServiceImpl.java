package ph.txtdis.dyvek.service;

import static java.util.Arrays.asList;
import static ph.txtdis.type.ModuleType.DELIVERY_TO_PURCHASE_ORDER;
import static ph.txtdis.type.ModuleType.DELIVERY_TO_SALES_ORDER;
import static ph.txtdis.type.ModuleType.PURCHASE_ORDER;
import static ph.txtdis.type.ModuleType.SALES_BILLING;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dyvek.info.ToDoReminder;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.info.Information;
import ph.txtdis.type.ModuleType;

@Service("reminderService")
public class ReminderServiceImpl //
		implements ReminderService {

	@Autowired
	private ClientBillAssignmentService clientBillAssignmentService;

	@Autowired
	private ClientBillingService clientBillService;

	@Autowired
	private PurchaseService purchaseService;

	@Autowired
	private VendorBillingService vendorService;

	private Queue<List<String>> toDoList;

	@Override
	public void checkThingToDo() throws Information, Exception {
		List<String> toDo = toDoList.element();
		throw new ToDoReminder(toDo);
	}

	@Override
	public List<String> getThingToDo() {
		return toDoList.poll();
	}

	@Override
	public void ignore() {
		toDoList.poll();
	}

	@Override
	public void setThingsToDo() {
		toDoList = new LinkedList<>();
		checkForUnassignedClientDeliveries();
		checkForUnbilledClientDeliveries();
		checkForExpiringNotFullyServedPurchaseOrders();
		checkForUnassignedVendorDeliveries();
	}

	private void checkForUnassignedClientDeliveries() {
		List<Billable> l = clientBillAssignmentService.list();
		if (l != null && !l.isEmpty())
			for (Billable b : l)
				addToThingsToDo(DELIVERY_TO_SALES_ORDER, b);
	}

	private void addToThingsToDo(ModuleType module, Billable b) {
		toDoList.add(asList(module.toString(), b.getId().toString()));
	}

	private void checkForUnbilledClientDeliveries() {
		List<Billable> l = clientBillService.list();
		if (l != null && !l.isEmpty())
			for (Billable b : l)
				addToThingsToDo(SALES_BILLING, b);
	}

	private void checkForExpiringNotFullyServedPurchaseOrders() {
		List<Billable> l = purchaseService.findExpiringTheFollowingDayOrHaveExpired();
		if (l != null && !l.isEmpty())
			for (Billable b : l)
				addToThingsToDo(PURCHASE_ORDER, b);
	}

	private void checkForUnassignedVendorDeliveries() {
		List<Billable> l = vendorService.list();
		if (l != null && !l.isEmpty())
			for (Billable b : l)
				addToThingsToDo(DELIVERY_TO_PURCHASE_ORDER, b);
	}
}
