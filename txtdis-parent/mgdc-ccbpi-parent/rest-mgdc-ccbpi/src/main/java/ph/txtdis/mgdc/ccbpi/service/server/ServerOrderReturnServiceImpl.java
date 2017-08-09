package ph.txtdis.mgdc.ccbpi.service.server;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.apache.log4j.Logger.getLogger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Bom;
import ph.txtdis.mgdc.ccbpi.domain.BillableDetailEntity;
import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;
import ph.txtdis.mgdc.ccbpi.domain.BomEntity;
import ph.txtdis.mgdc.ccbpi.repository.BillingDetailRepository;
import ph.txtdis.mgdc.ccbpi.repository.OrderConfirmationRepository;
import ph.txtdis.mgdc.ccbpi.repository.OrderReturnRepository;
import ph.txtdis.type.OrderConfirmationType;
import ph.txtdis.type.TransactionDirectionType;

@Service("orderReturnService")
public class ServerOrderReturnServiceImpl //
	extends AbstractSpunSavedBillableService //
	implements OrderReturnService,
	ReceivableService,
	ReceivingService,
	QtyPerItemService {

	private static final String MANUAL = OrderConfirmationType.MANUAL.toString();

	private static final String PARTIAL = OrderConfirmationType.PARTIAL.toString();

	private static final String WAREHOUSE = OrderConfirmationType.WAREHOUSE.toString();

	private static Logger logger = getLogger(ServerOrderReturnServiceImpl.class);

	@Autowired
	private BillingDetailRepository detailRepository;

	@Autowired
	private OrderConfirmationRepository ocsRepository;

	@Autowired
	private OrderReturnRepository orderReturnRepository;

	@Autowired
	private BomService bomService;

	@Override
	protected BillableEntity create(Billable b) {
		BillableEntity e = super.create(b);
		return setAllReceivingData(e, b);
	}

	private BillableEntity setAllReceivingData(BillableEntity e, Billable b) {
		e = setReceivingData(e, b, repository);
		return setModifiedReceivingData(e, b);
	}

	private BillableEntity setModifiedReceivingData(BillableEntity e, Billable b) {
		if (b.getReceivingModifiedBy() == null || e.getReceivingModifiedBy() != null)
			return e;
		e.setReceivingModifiedBy(b.getReceivingModifiedBy());
		e.setReceivingModifiedOn(nowIfReceivingModifiedOnNull(b));
		return e;
	}

	private ZonedDateTime nowIfReceivingModifiedOnNull(Billable b) {
		ZonedDateTime t = b.getReceivingModifiedOn();
		return t != null ? t : ZonedDateTime.now();
	}

	@Override
	public List<Bom> extractAll(Long itemId, String itemName, BigDecimal qty) {
		return bomService.extractAll(itemId, itemName, qty);
	}

	@Override
	public Billable find(LocalDate orderDate, Long customerVendorId, Long orderCount) {
		BillableEntity b = findOrderReturn(orderDate, customerVendorId, orderCount);
		return toModel(b);
	}

	private BillableEntity findOrderReturn(LocalDate orderDate, Long customerVendorId, Long orderCount) {
		if (orderCount == 0)
			return ocsRepository.findFirstByCustomerVendorIdAndOrderDateOrderByBookingIdDesc( //
				customerVendorId, orderDate);
		return ocsRepository.findByCustomerVendorIdAndOrderDateAndBookingId(customerVendorId, orderDate, orderCount);
	}

	@Override
	public List<BillableDetailEntity> getDetailEntityList(String itemVendorNo,
	                                                      String route,
	                                                      LocalDate start,
	                                                      LocalDate end) {
		List<BillableDetailEntity> list = detailRepository
			.findByItemVendorIdAndReturnedQtyGreaterThanAndBillingPrefixNotNullAndBillingPrefixNotInAndBillingSuffixNotNullAndBillingSuffixContainingAndBillingDueDateBetween(
				itemVendorNo, ZERO, ocsTypes(start, end), routeName(route), start, end);
		logger.info("\n    OrderReturnDetails = " + list);
		return list == null ? emptyList() : list;
	}

	private List<String> ocsTypes(LocalDate start, LocalDate end) {
		return start.isEqual(end) ? asList(MANUAL, PARTIAL, WAREHOUSE) : Arrays.asList("");
	}

	private String routeName(String route) {
		return route.equalsIgnoreCase("ALL") ? "" : route;
	}

	@Override
	public List<BomEntity> getOrderNoAndCustomerAndRouteGroupedBomList() {
		List<BillableEntity> l = orderReturnRepository
			.findByCustomerNotNullAndPrefixAndSuffixNotNullAndBookingIdNotNullAndReceivedOnNotNull(PARTIAL);
		logger.info("\n    OrderConfirmations = " + l);
		return l == null ? Collections.emptyList() : toReturnedOrderNoAndCustomerAndRouteGroupedBomList(l);
	}

	@Override
	public List<BomEntity> getBomList(LocalDate start, LocalDate end) {
		List<BillableEntity> l = orderReturnRepository
			.findByCustomerNotNullAndPrefixNotNullAndPrefixNotInAndSuffixNotNullAndBookingIdNotNullAndDueDateBetweenAndReceivedOnNotNull(
				Arrays.asList(WAREHOUSE, PARTIAL), start, end);
		logger.info("\n    OrderConfirmations = " + l);
		return l == null ? Collections.emptyList() : toReturnedBomList(l);
	}

	@Override
	protected BillableEntity firstEntity() {
		return orderReturnRepository
			.findFirstByCustomerNotNullAndPrefixNotNullAndSuffixNotNullAndBookingIdNotNullAndReceivedOnNotNullOrderByIdAsc();
	}

	@Override
	protected BillableEntity lastEntity() {
		return orderReturnRepository
			.findFirstByCustomerNotNullAndPrefixNotNullAndSuffixNotNullAndBookingIdNotNullAndReceivedOnNotNullOrderByIdDesc();
	}

	@Override
	public List<BomEntity> listBadItemsIncomingQty(LocalDate start, LocalDate end) {
		List<BillableEntity> l = listWithReceiptEntities(start, end);
		if (l == null)
			return null;
		l = l.stream().filter(e -> e.getRma() != null && e.getRma() == false).collect(Collectors.toList());
		return toBomList(TransactionDirectionType.INCOMING, l);
	}

	private List<BillableEntity> listWithReceiptEntities(LocalDate start, LocalDate end) {
		return repository.findByOrderDateBetweenAndReceivedOnNotNull(start, end);
	}

	@Override
	public List<BomEntity> listBadItemsOutgoingQty(LocalDate start, LocalDate end) {
		List<BillableEntity> l = listPicked(start, end);
		if (l == null)
			return null;
		l = l.stream().filter(e -> e.getRma() != null && e.getRma() == false).collect(Collectors.toList());
		return toBomList(TransactionDirectionType.OUTGOING, l);
	}

	private List<BillableEntity> listPicked(LocalDate start, LocalDate end) {
		return repository.findByOrderDateBetweenAndPickingNotNull(start, end);
	}

	@Override
	public List<BomEntity> listGoodItemsIncomingQty(LocalDate start, LocalDate end) {
		List<BillableEntity> l = listWithReceiptEntities(start, end);
		if (l == null)
			return null;
		l = l.stream().filter(e -> e.getRma() == null || e.getRma() == true).collect(Collectors.toList());
		return toBomList(TransactionDirectionType.INCOMING, l);
	}

	@Override
	public List<BomEntity> listGoodItemsOutgoingQty(LocalDate start, LocalDate end) {
		List<BillableEntity> l = listPicked(start, end);
		if (l == null)
			return null;
		l = l.stream().filter(e -> e.getRma() == null || e.getRma() == true).collect(Collectors.toList());
		return toBomList(TransactionDirectionType.OUTGOING, l);
	}

	@Override
	protected BillableEntity nextEntity(Long id) {
		return orderReturnRepository
			.findFirstByCustomerNotNullAndPrefixNotNullAndSuffixNotNullAndBookingIdNotNullAndReceivedOnNotNullAndIdGreaterThanOrderByIdAsc(
				id);
	}

	@Override
	protected BillableEntity previousEntity(Long id) {
		return orderReturnRepository
			.findFirstByCustomerNotNullAndPrefixNotNullAndSuffixNotNullAndBookingIdNotNullAndReceivedOnNotNullAndIdLessThanOrderByIdDesc(
				id);
	}

	@Override
	public BillableDetailEntity setTheTotalOfTheMappedEntityAndModelDetailsItemQuantities(BillableDetailEntity e,
	                                                                                      BillableDetail b) {
		BigDecimal total = b.getReturnedQty().add(b.getReturnedQty());
		e.setReturnedQty(total);
		return e;
	}

	@Override
	protected BillableEntity update(BillableEntity e, Billable b) {
		e = super.update(e, b);
		e = addReceivedQtyToDetails(e, b);
		e.setReceivingModifiedBy(b.getReceivingModifiedBy());
		return setReceivingData(e, b, repository);
	}

	private BillableEntity addReceivedQtyToDetails(BillableEntity e, Billable b) {
		return updateDetailQty(e, b);
	}
}