package ph.txtdis.mgdc.ccbpi.service.server;

import static java.math.BigDecimal.ZERO;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.QuantityType.ACTUAL;
import static ph.txtdis.type.QuantityType.EXPECTED;
import static ph.txtdis.type.QuantityType.OTHER;
import static ph.txtdis.type.UomType.CS;
import static ph.txtdis.util.DateTimeUtils.toOrderConfirmationDate;
import static ph.txtdis.util.NumberUtils.divide;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.mgdc.ccbpi.domain.BillableDetailEntity;
import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;
import ph.txtdis.mgdc.ccbpi.domain.CustomerEntity;
import ph.txtdis.type.QuantityType;

@Service("remittanceVarianceService")
public class RemittanceVarianceServiceImpl //
		implements RemittanceVarianceService {

	private static Logger logger = getLogger(RemittanceVarianceServiceImpl.class);

	@Autowired
	private PickListService pickListService;

	@Autowired
	private DetailsToVarianceService varianceService;

	@Autowired
	private OrderConfirmationService orderConfirmationService;

	@Autowired
	private QtyPerUomService uomService;

	@Autowired
	private UnpickedOrderService unpickedOrderService;

	@Override
	public List<SalesItemVariance> list(String collector, LocalDate start, LocalDate end) {
		logger.info("\n    CollectorStartEnd@list = " + collector + ", " + start + ", " + end);
		varianceService.addOtherService(unpickedOrderService);
		varianceService.addExpectedService(pickListService);
		varianceService.addActualService(null);
		varianceService.addReturnedService(pickListService);
		return varianceService.list(collector, start, end);
	}

	@Override
	public List<SalesItemVariance> listDelivered(String route, LocalDate start, LocalDate end) {
		List<BillableEntity> ocs = orderConfirmationService.listDelivered(collector(route), start, end);
		logger.info("\n    Delivered@listDelivered = " + ocs);
		return toSalesItemVariance(ocs, ACTUAL);
	}

	private String collector(String route) {
		return route.equalsIgnoreCase("ALL") ? "" : route;
	}

	private List<SalesItemVariance> toSalesItemVariance(List<BillableEntity> ocs, QuantityType type) {
		return ocs == null ? null //
				: ocs.stream().map(e -> toSalesItemVariance(e, type)).collect(Collectors.toList());
	}

	private SalesItemVariance toSalesItemVariance(BillableEntity e, QuantityType type) {
		SalesItemVariance v = new SalesItemVariance();
		v.setId(e.getId());
		v.setOrderNo(orderNo(e));
		v.setCustomer(outlet(e));
		v.setExpectedCount(totalCount(e, type));
		v.setPriceValue(averageUnitPrice(e, type));
		v.setQtyPerCase(1);
		return v;
	}

	private String outlet(BillableEntity e) {
		return customer(e).getName();
	}

	private CustomerEntity customer(BillableEntity e) {
		return e.getCustomer();
	}

	private String orderNo(BillableEntity e) {
		return customerVendorNo(e) + "-" + ocsDate(e) + "/" + ocsSequenceNo(e);
	}

	private Long customerVendorNo(BillableEntity e) {
		return customer(e).getVendorId();
	}

	private String ocsDate(BillableEntity e) {
		return toOrderConfirmationDate(e.getOrderDate());
	}

	private Long ocsSequenceNo(BillableEntity e) {
		return e.getBookingId();
	}

	private int totalCount(BillableEntity e, QuantityType type) {
		return totalQty(e, type).intValue();
	}

	private BigDecimal totalQty(BillableEntity e, QuantityType type) {
		List<BillableDetailEntity> details = e.getDetails();
		return details == null ? ZERO //
				: details.stream() //
						.map(d -> qtyInCases(d, type)) //
						.reduce(ZERO, BigDecimal::add);
	}

	private BigDecimal qtyInCases(BillableDetailEntity d, QuantityType type) {
		return type == ACTUAL ? finalQtyInCases(d) : initialQtyInCases(d);
	}

	private BigDecimal finalQtyInCases(BillableDetailEntity d) {
		return divide(d.getFinalQty(), uomService.getItemQtyPerUom(d.getItem(), CS));
	}

	private BigDecimal initialQtyInCases(BillableDetailEntity d) {
		return divide(d.getInitialQty(), uomService.getItemQtyPerUom(d.getItem(), CS));
	}

	private BigDecimal averageUnitPrice(BillableEntity e, QuantityType type) {
		return divide(e.getTotalValue(), totalQty(e, type));
	}

	@Override
	public List<SalesItemVariance> listLoaded(String route, LocalDate start, LocalDate end) {
		List<BillableEntity> ocs = orderConfirmationService.list(collector(route), start, end);
		logger.info("\n    Loaded@listLoaded = " + ocs);
		return toSalesItemVariance(ocs, EXPECTED);
	}

	@Override
	public List<SalesItemVariance> listUnpicked(String route, LocalDate start, LocalDate end) {
		List<BillableEntity> ocs = unpickedOrderService.list(collector(route), start, end);
		logger.info("\n    Unpicked@listUnpicked = " + ocs);
		return toSalesItemVariance(ocs, OTHER);
	}
}