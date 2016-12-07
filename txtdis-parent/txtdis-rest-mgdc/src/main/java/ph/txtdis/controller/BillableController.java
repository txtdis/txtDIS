package ph.txtdis.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.sql.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.dto.Billable;
import ph.txtdis.service.BillableService;

@RequestMapping("/billables")
@RestController("billableController")
public class BillableController extends AbstractBillableController<BillableService> {

	@RequestMapping(path = "/aged", method = GET)
	public ResponseEntity<?> listAged(@RequestParam("customer") Long id) {
		List<Billable> l = service.listAgedBillables(id);
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/aging", method = GET)
	public ResponseEntity<?> listAging(@RequestParam("customer") Long id) {
		List<Billable> l = service.listAgingBillables(id);
		return new ResponseEntity<>(l, OK);
	}

	@RequestMapping(path = "/badOrder", method = GET)
	public ResponseEntity<?> findBadOrderById(@RequestParam("id") Long id) {
		Billable b = service.findBadOrderById(id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/badOrderReceipt", method = GET)
	public ResponseEntity<?> findBadOrderReceiptById(@RequestParam("id") Long id) {
		Billable b = service.findBadOrderReceiptById(id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/badOrders", method = GET)
	public ResponseEntity<?> findBadOrderByCustomer(@RequestParam("customer") Long id) {
		Billable b = service.findBadOrderByCustomerId(id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/billable", method = GET)
	public ResponseEntity<?> findById(@RequestParam("id") Long id) {
		Billable b = service.findBillableById(id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/billed", method = GET)
	public ResponseEntity<?> findBilledByCustomer(@RequestParam("customer") Long id) {
		Billable b = service.findBilledByCustomerId(id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/booking", method = GET)
	public ResponseEntity<?> findBookingById(@RequestParam("id") Long id) {
		Billable b = service.findBookingById(id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/deliveryReport", method = GET)
	public ResponseEntity<?> findDeliveryReportById(@RequestParam("id") Long id) {
		Billable b = service.findDeliveryReportById(id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/find", method = GET)
	public ResponseEntity<?> findByOrderNo(@RequestParam("prefix") String p, @RequestParam("id") Long id,
			@RequestParam("suffix") String s) {
		Billable b = service.findByOrderNo(p, id, s);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/item", method = GET)
	public ResponseEntity<?> findByItem(@RequestParam("id") Long itemId, @RequestParam("customer") Long customerId,
			@RequestParam("date") Date d) {
		Billable b = service.findByItemId(itemId, customerId, d);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/latest", method = GET)
	public ResponseEntity<?> findLatest(@RequestParam("prefix") String prefix, @RequestParam("suffix") String suffix,
			@RequestParam("startId") Long startId, @RequestParam("endId") Long endId) {
		Billable b = service.findLatest(prefix, suffix, startId, endId);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/loadOrder", method = GET)
	public ResponseEntity<?> findLoadOrderById(@RequestParam("id") Long id) {
		Billable b = service.findLoadOrderById(id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/notFullyPaidCOD", method = GET)
	public ResponseEntity<?> findNotFullyPaidCOD(@RequestParam("seller") String s, @RequestParam("upTo") Date d) {
		Billable b = service.findNotFullyPaidCOD(s, d);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/pendingReturn", method = GET)
	public ResponseEntity<?> findOpenBadOrReturnOrderByCustomer(@RequestParam("customer") Long customerId) {
		Billable b = service.findOpenBadOrReturnOrderByCustomerId(customerId);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/purchased", method = GET)
	public ResponseEntity<?> findByCustomerPurchaseItem(@RequestParam("by") Long customerId,
			@RequestParam("item") Long itemId) {
		Billable b = service.findByCustomerPurchasedItemId(customerId, itemId);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/purchaseOrder", method = GET)
	public ResponseEntity<?> findPurchaseOrderById(@RequestParam("id") Long id) {
		Billable b = service.findPurchaseOrderById(id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/purchaseReceipt", method = GET)
	public ResponseEntity<?> findPurchaseReceiptById(@RequestParam("id") Long id) {
		Billable b = service.findPurchaseReceiptById(id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/receivingReport", method = GET)
	public ResponseEntity<?> findReceivingReportById(@RequestParam("id") Long id) {
		Billable b = service.findReceivingReportById(id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/referenced", method = GET)
	public ResponseEntity<?> findByBookingId(@RequestParam("bookingId") Long id) {
		Billable b = service.findByBookingId(id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/returnOrder", method = GET)
	public ResponseEntity<?> findRmaById(@RequestParam("id") Long id) {
		Billable b = service.findRmaById(id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/returnOrders", method = GET)
	public ResponseEntity<?> findReturnOrderByCustomer(@RequestParam("customer") Long customerId) {
		Billable b = service.findReturnOrderByCustomerId(customerId);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/returnReceipt", method = GET)
	public ResponseEntity<?> findRmaReceiptById(@RequestParam("id") Long id) {
		Billable b = service.findRmaReceiptById(id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/salesOrder", method = GET)
	public ResponseEntity<?> findSalesOrderById(@RequestParam("id") Long id) {
		Billable b = service.findSalesOrderById(id);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/unbilled", method = GET)
	public ResponseEntity<?> findUnbilledPickedUpTo(@RequestParam("seller") String s, @RequestParam("upTo") Date d) {
		Billable b = service.findUnbilledPickedUpTo(s, d);
		return new ResponseEntity<>(b, OK);
	}

	@RequestMapping(path = "/unpicked", method = GET)
	public ResponseEntity<?> findUnpickedOn(@RequestParam("date") Date d) {
		List<Billable> l = service.findUnpickedOn(d);
		return new ResponseEntity<>(l, OK);
	}
}