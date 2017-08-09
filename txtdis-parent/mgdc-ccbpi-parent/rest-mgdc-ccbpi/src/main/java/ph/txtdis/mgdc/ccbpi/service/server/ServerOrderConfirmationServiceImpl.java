package ph.txtdis.mgdc.ccbpi.service.server;

import static java.math.BigDecimal.ZERO;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.util.NumberUtils.toCurrencyText;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.ccbpi.domain.BillableDetailEntity;
import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;
import ph.txtdis.mgdc.ccbpi.domain.BomEntity;
import ph.txtdis.mgdc.ccbpi.domain.ItemEntity;
import ph.txtdis.mgdc.ccbpi.repository.BillingDetailRepository;
import ph.txtdis.mgdc.ccbpi.repository.OrderConfirmationRepository;
import ph.txtdis.type.OrderConfirmationType;
import ph.txtdis.type.PriceType;
import ph.txtdis.type.QuantityType;
import ph.txtdis.util.DateTimeUtils;

@Service("orderConfirmationService")
public class ServerOrderConfirmationServiceImpl //
	extends AbstractSpunSavedBillableService //
	implements OrderConfirmationService {

	private static final String MANUAL = OrderConfirmationType.MANUAL.toString();

	private static final String PARTIAL = OrderConfirmationType.PARTIAL.toString();

	private static final String REGULAR = OrderConfirmationType.REGULAR.toString();

	private static final String UNDELIVERED = OrderConfirmationType.UNDELIVERED.toString();

	private static final String WAREHOUSE = OrderConfirmationType.WAREHOUSE.toString();

	private static Logger logger = getLogger(ServerOrderConfirmationServiceImpl.class);

	@Autowired
	private BillingDetailRepository detailRepository;

	@Autowired
	private DetailsToBomService bomService;

	@Autowired
	private ServerEmptiesItemService emptiesService;

	@Autowired
	private OrderConfirmationRepository ocsRepository;

	@Override
	public BillableEntity findEntityByBookingNo(String ocsNo) {
		return findOrderConfirmation(orderDate(ocsNo), customerVendorId(ocsNo), orderCount(ocsNo));
	}

	private BillableEntity findOrderConfirmation(LocalDate orderDate, Long customerVendorId, Long orderCount) {
		if (orderCount == 0)
			return ocsRepository.findFirstByCustomerVendorIdAndOrderDateOrderByBookingIdDesc(customerVendorId, orderDate);
		return ocsRepository.findByCustomerVendorIdAndOrderDateAndBookingId(customerVendorId, orderDate, orderCount);
	}

	private LocalDate orderDate(String id) {
		return LocalDate.parse( //
			StringUtils.substringBetween(id, "-", "/"), //
			DateTimeUtils.orderConfirmationFormat());
	}

	private Long customerVendorId(String id) {
		return Long.valueOf(StringUtils.substringBefore(id, "-"));
	}

	private Long orderCount(String id) {
		return Long.valueOf(StringUtils.substringAfter(id, "/"));
	}

	@Override
	public Billable find(LocalDate orderDate, Long customerVendorId, Long orderCount) {
		BillableEntity b = findOrderConfirmation(orderDate, customerVendorId, orderCount);
		return toModel(b);
	}

	@Override
	public Billable toModel(BillableEntity e) {
		Billable b = super.toModel(e);
		if (b != null)
			b.setRoute(routeName(e));
		return b;
	}

	private String routeName(BillableEntity b) {
		try {
			return b.getCustomer().getRoute().getName();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<Billable> findAllUnpicked(LocalDate d) {
		List<BillableEntity> l = ocsRepository
			.findByCustomerNotNullAndPrefixInAndDueDateAndPickingNull(
				Arrays.asList(MANUAL, PARTIAL, REGULAR, UNDELIVERED, WAREHOUSE), d);
		logger.info("\n    UnpickedOrderConfirmations = " + l);
		return toModels(l);
	}

	@Override
	public List<BomEntity> getBomList(String route, LocalDate start, LocalDate end) {
		List<BillableEntity> l = orderConfirmationList(route, start, end);
		logger.info("\n    OrderConfirmations = " + l);
		return l == null ? Collections.emptyList() : toBomList(l);
	}

	private List<BillableEntity> orderConfirmationList(String route, LocalDate start, LocalDate end) {
		return ocsRepository.findByCustomerNotNullAndPrefixAndSuffixContainingAndDueDateBetween( //
			REGULAR, routeName(route), start, end);
	}

	private String routeName(String route) {
		return route.equalsIgnoreCase("ALL") ? "" : route;
	}

	@Override
	public List<BomEntity> getDeliveredOrderNoAndCustomerAndRouteGroupedBomList() {
		List<BillableEntity> l = ocsRepository.findByCustomerNotNull(PARTIAL);
		logger.info("\n    OrderConfirmations for Partial Delivery = " + l);
		return l == null ? Collections.emptyList() : toInitialOrderNoAndCustomerAndRouteGroupedBomList(l);
	}

	@Override
	public List<BillableDetailEntity> getDetailEntityList(String itemVendorNo,
	                                                      String route,
	                                                      LocalDate start,
	                                                      LocalDate end) {
		List<BillableDetailEntity> list =
			detailRepository.findByItemVendorIdAndBillingPrefixAndBillingSuffixContainingAndBillingDueDateBetween(
				itemVendorNo, REGULAR, routeName(route), start, end);
		logger.info("\n    OrderConfirmationDetails = " + list);
		return list == null ? Collections.emptyList() : list;
	}

	@Override
	public Billable getWithDeliveredValue(String collector, LocalDate start, LocalDate end) {
		List<BillableEntity> l = list(collector, start, end);
		return getWithDeliveredValue(l);
	}

	@Override
	public List<BillableEntity> list(String collector, LocalDate start, LocalDate end) {
		List<BillableEntity> ocs =
			ocsRepository.findByCustomerNotNullAndDueDateBetweenAndPickingLeadAssistantNameContaining( //
				start, end, collector);
		logger.info("\n    LoadedOrderConfirmations@listLoaded = " + ocs);
		return ocs;
	}

	private Billable getWithDeliveredValue(List<BillableEntity> l) {
		BigDecimal v = contentsAndEmptiesValue(l);
		logger.info("\n    ContentsPlusEmpties@getWithDeliveredValue = " + v);
		return totalOnlyBillable(v);
	}

	private BigDecimal contentsAndEmptiesValue(List<BillableEntity> l) {
		BigDecimal contents = contentsValue(l);
		BigDecimal empties = emptiesValue(l);
		logger
			.info("\n    Value@contentsAndEmptiesValue = " + toCurrencyText(contents) + " & " + toCurrencyText(empties));
		return contents.add(empties);
	}

	private Billable totalOnlyBillable(BigDecimal v) {
		Billable b = new Billable();
		b.setTotalValue(v);
		return b;
	}

	private BigDecimal contentsValue(List<BillableEntity> l) {
		return l == null ? BigDecimal.ZERO //
			: l.stream().map(b -> b.getTotalValue()).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private BigDecimal emptiesValue(List<BillableEntity> billables) {
		List<BomEntity> boms = bomService.toEmptiesBomList(QuantityType.ACTUAL, billables);
		Map<ItemEntity, BigDecimal> map = emptiesService.mapEmptiesPriceValue(PriceType.DEALER, boms);
		return map.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	@Override
	public List<BillableEntity> listDelivered(String route, LocalDate start, LocalDate end) {
		List<BillableEntity> ocs = ocsRepository
			.findByCustomerNotNullAndDueDateBetweenAndPickingLeadAssistantNameContainingAndTotalValueGreaterThan( //
				start, end, route, ZERO);
		logger.info("\n    DeliveredOrderConfirmations@listDelivered = " + ocs);
		return ocs;
	}

	@Override
	public List<BillableEntity> listUnpicked(String route, LocalDate start, LocalDate end) {
		List<BillableEntity> ocs =
			ocsRepository.findByCustomerNotNullAndDueDateBetweenAndPickingLeadAssistantNameContaining( //
				start, end, route);
		logger.info("\n    LoadedOrderConfirmations@listLoaded = " + ocs);
		return ocs;
	}

	@Override
	protected BillableEntity firstEntity() {
		return ocsRepository.findFirstByCustomerNotNullOrderByIdAsc();
	}

	@Override
	protected BillableEntity nextEntity(Long id) {
		return ocsRepository.findFirstByCustomerNotNullAndIdGreaterThanOrderByIdAsc(id);
	}

	@Override
	protected BillableEntity lastEntity() {
		return ocsRepository.findFirstByCustomerNotNullOrderByIdDesc();
	}

	@Override
	protected BillableEntity previousEntity(Long id) {
		return ocsRepository.findFirstByCustomerNotNullAndIdLessThanOrderByIdDesc(id);
	}
}