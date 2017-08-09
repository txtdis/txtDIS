package ph.txtdis.mgdc.gsm.service.server;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ph.txtdis.domain.SyncEntity;
import ph.txtdis.dto.*;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.domain.PickListEntity;
import ph.txtdis.mgdc.gsm.domain.RemittanceEntity;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.repository.SyncRepository;
import ph.txtdis.service.SyncService;
import ph.txtdis.type.SyncType;

import java.util.List;

import static java.time.ZonedDateTime.now;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.groupingBy;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.UserType.DRIVER;
import static ph.txtdis.type.UserType.HELPER;
import static ph.txtdis.util.DateTimeUtils.toTimestampText;
import static ph.txtdis.util.DateTimeUtils.toUtilDate;

@Component("edmsDataCorrectionTask01")
public class EdmsDataCorrectionTask01Impl //
	implements EdmsDataCorrectionTask01 {

	private static Logger logger = getLogger(EdmsDataCorrectionTask01Impl.class);

	@Autowired
	private SyncRepository syncRepository;

	@Autowired
	private CreditedAndDiscountedCustomerService customerService;

	@Autowired
	private InvoiceService billingService;

	@Autowired
	private PickListService pickingService;

	@Autowired
	private GsmPurchaseReceiptService purchaseReceiptService;

	@Autowired
	private GsmRouteService routeService;

	@Autowired
	private UserService userService;

	@Autowired
	private ImportedItemService itemService;

	@Autowired
	private ImportedTruckService truckService;

	@Autowired
	private GsmRemittanceService remitService;

	@Autowired
	private ScheduledCustomerValidationTask customerValidationService;

	//	@Autowired
	//	private ScheduledRemittanceValidationTask remittanceValidationService;

	@Autowired
	private SyncService syncService;

	@Override
	@Transactional
	@Scheduled(initialDelay = 1000, fixedRate = 99999999)
	public void update() {
		if (syncService.getUpdateVersion().equalsIgnoreCase("0.0.0.0")) {
			logger.info("\n    Started updates ");
			//remittanceValidationService
			// .voidAllUnvalidatedAfterPrescribedPeriodsSincePaymentAndCreationHaveBothExpired();
			logger.info("\n     rebuildPickings");
			rebuildPicking();
			logger.info("\n     voidAllPaidByCreditNotes");
			voidAllPaidByCreditNotes();
			logger.info("\n     correctDrivers");
			correctDrivers();
			logger.info("\n     correctHelpers");
			correctHelpers();
			logger.info("\n     correctPrices");
			correctPrices();
			logger.info("\n     correctTrucks");
			correctTrucks();
			logger.info("\n     correctRoutes");
			correctRoutes();
			logger.info("\n     correctOutlets");
			correctOutlets();
			logger.info("\n     correctPickings");
			correctPickings();
			logger.info("\n     correctBillings");
			correctBillings();
			logger.info("\n     correctPurchaseReceipts");
			correctPurchaseReceipts();
			logger.info("\n     correctDeliveryReceipts");
			correctRRs();
			logger.info("\n     saveEdmsUpdateSync");
			syncRepository.save(edmsUpdateSync());
			logger.info("\n     Completed updates");
		}
	}

	private void rebuildPicking() {
		List<Billable> l = billingService.findAllBilledButUnpicked();
		if (l != null)
			l.stream() //
				.filter(b -> b.getRoute() != null && !b.getRoute().isEmpty())
				.collect(groupingBy( //
					Billable::getOrderDate, //
					groupingBy(Billable::getRoute))) //
				.entrySet() //
				.forEach(s -> s.getValue().values() //
					.forEach(b -> savePickListsAndBillables(b)));
	}

	private void voidAllPaidByCreditNotes() {
		List<RemittanceEntity> l = remitService.findAllByDraweeBank("CREDIT MEMO");
		if (l != null) {
			for (RemittanceEntity r : l)
				remitService.updatePaymentBasedOnValidation( //
					"", //
					r.getId().toString(), //
					"false", //
					"INVALID -- NO REFERENCE C/N#", //
					"SYSGEN", //
					toTimestampText(now()));
		}
	}

	private void correctDrivers() {
		List<User> l = userService.listNamesByRole(asList(DRIVER));
		for (User u : l)
			try {
				userService.saveDriver(u);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
	}

	private void correctHelpers() {
		List<User> l = userService.listNamesByRole(asList(HELPER));
		for (User u : l)
			try {
				userService.saveHelper(u);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
	}

	private void correctPrices() {
		List<Item> l = itemService.listFully();
		for (Item i : l)
			try {
				if (i.getVendorNo() != null)
					itemService.saveToEdms(i);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
	}

	private void correctTrucks() {
		List<Truck> l = truckService.list();
		for (Truck t : l)
			try {
				truckService.saveToEdms(t);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
	}

	private void correctRoutes() {
		List<Route> l = routeService.list();
		for (Route r : l)
			try {
				routeService.saveToEdms(r);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
	}

	private void correctOutlets() {
		customerValidationService.cancelAllCustomerDiscountsIfMonthlyAverageIsLessThanRequired();
		customerValidationService.deactiveAllNonBuyingOutletsAfterThePrescribedPeriod();
		List<Customer> l = customerService.listOutlets();
		for (Customer c : l)
			try {
				if (c.getParent() == null)
					customerService.saveToEdms(c);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
	}

	private void correctPickings() {
		List<PickList> l = pickingService.findAll();
		savePickings(l);
	}

	private void correctBillings() {
		List<Billable> l = billingService.findAllOutletBillings();
		saveBillablesToEdms(l);
	}

	private void correctPurchaseReceipts() {
		List<Billable> l = purchaseReceiptService.list();
		if (l != null)
			saveBillablesToEdms(l);
	}

	private void correctRRs() {
		List<PickList> l = pickingService.findAllWithReturns();
		savePickings(l);
	}

	private SyncEntity edmsUpdateSync() {
		SyncEntity s = new SyncEntity();
		s.setLastSync(toUtilDate("2000-01-01"));
		s.setType(SyncType.UPDATE);
		return s;
	}

	private void savePickListsAndBillables(List<Billable> l) {
		PickListEntity p = savePickList(l.get(0));
		l.forEach(b -> savePickedBillable(b, p));
	}

	private void savePickings(List<PickList> l) {
		for (PickList p : l)
			try {
				pickingService.saveToEdms(p);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
	}

	private void saveBillablesToEdms(List<Billable> l) {
		for (Billable b : l)
			try {
				billingService.saveToEdms(b);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
	}

	private PickListEntity savePickList(Billable b) {
		PickListEntity p = new PickListEntity();
		p.setPickDate(b.getOrderDate());
		p.setLeadAssistant(userService.findEntityByPrimaryKey(b.getSeller()));
		return pickingService.post(p);
	}

	private void savePickedBillable(Billable b, PickListEntity p) {
		BillableEntity e = billingService.findEntityByPrimaryKey(b.getId());
		e.setPicking(p);
		billingService.post(e);
	}
}
