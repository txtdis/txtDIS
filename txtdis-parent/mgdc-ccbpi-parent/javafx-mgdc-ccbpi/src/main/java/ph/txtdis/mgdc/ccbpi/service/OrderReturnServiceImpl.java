package ph.txtdis.mgdc.ccbpi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.PickList;
import ph.txtdis.dto.ReceivingDetail;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.service.PickListService;
import ph.txtdis.type.OrderConfirmationType;
import ph.txtdis.type.OrderReturnType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.type.OrderReturnType.INVALID;
import static ph.txtdis.type.OrderReturnType.OWN_FAULT;
import static ph.txtdis.util.NumberUtils.isPositive;
import static ph.txtdis.util.UserUtils.username;

@Service("orderReturnService")
public class OrderReturnServiceImpl //
	extends AbstractOrderConfirmationService //
	implements OrderReturnService {

	@Autowired
	private OrderConfirmationService ocsService;

	@Autowired
	private PickListService pickListService;

	private BillableDetail receivingDetail;

	private List<BillableDetail> bookedDetails;

	@Override
	protected Billable findByOrderDateAndOutletIdAndSequenceId(LocalDate orderDate, Long outletId, Long sequenceId)
		throws Exception {
		return orderService().getOne("/rr?date=" + orderDate + "&outletId=" + outletId + "&count=" + sequenceId);
	}

	@Override
	public String getAlternateName() {
		return "R/R";
	}

	@Override
	public List<? extends ReceivingDetail> getPickedDetails() {
		return bookedDetails;
	}

	@Override
	public String getCollectorName() {
		try {
			PickList p = (PickList) pickListService.findById(get().getPickListId());
			return p.getLeadAssistant();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<BillableDetail> getDetails() {
		List<BillableDetail> l = super.getDetails();
		return l == null ? null
			: l.stream() //
			.filter(d -> d != null && isPositive(d.getReturnedQty())) //
			.collect(toList());
	}

	@Override
	public String getHeaderName() {
		return "Order Return";
	}

	@Override
	public String getModuleName() {
		return "orderReturn";
	}

	@Override
	@SuppressWarnings("unchecked")
	public BillableDetail getReceivingDetail() {
		return receivingDetail;
	}

	@Override
	public void setReceivingDetail(ReceivingDetail detail) {
		receivingDetail = (BillableDetail) detail;
	}

	@Override
	public String getRemarks() {
		String remarks = get().getRemarks();
		return remarks == null ? "" : remarks;
	}

	@Override
	public List<BigDecimal> getTotals(List<BillableDetail> l) {
		return asList(totalQty(l), totalValue(l));
	}

	private BigDecimal totalQty(List<BillableDetail> l) {
		return l.stream() //
			.filter(d -> d != null) //
			.map(BillableDetail::getReturnedQtyInCases) //
			.reduce(ZERO, BigDecimal::add);
	}

	private BigDecimal totalValue(List<BillableDetail> l) {
		return l.stream() //
			.filter(d -> d != null) //
			.map(BillableDetail::getReturnedSubtotalValue) //
			.reduce(ZERO, BigDecimal::add);
	}

	@Override
	public List<OrderReturnType> listReasons() {
		String reason = get().getReceivingModifiedBy();
		return reason != null //
			? asList(OrderReturnType.valueOf(reason.replace(" ", "_"))) //
			: asList(OrderReturnType.values());
	}

	@Override
	public void openByOpenDialogInputtedKey(String key) throws Exception {
		set(findByOrderNo(key));
	}

	@Override
	public void reset() {
		super.reset();
		receivingDetail = null;
		bookedDetails = emptyList();
	}

	@Override
	public void save() throws Information, Exception {
		get().setReceivedBy(username());
		super.save();
	}

	@Override
	public void setOrderUponValidation(String ocsId) throws Exception {
		Billable b = ocsService.findByOrderNo(ocsId);
		bookedDetails = b.getDetails();
		b.setDetails(null);
		set(b);
	}

	@Override
	protected Billable throwNotFoundExceptionIfNull(Billable b, String id) throws Exception {
		if (b == null)
			throw new InvalidException(getAbbreviatedModuleNoPrompt() + id + " not found;\n" //
				+ "Ensure ID's " + getOpenDialogPrompt().replaceFirst("F", "f"));
		return b;
	}

	@Override
	public void updateUponReasonValidation(OrderReturnType reason) throws Exception {
		if (!isNew() || reason == null || bookedDetails.isEmpty())
			return;
		if (reason == INVALID && isUndelivered())
			reason = OWN_FAULT;
		returnAll();
		setReason(reason);
	}

	@Override
	public boolean isNew() {
		return getReceivedOn() == null;
	}

	private boolean isUndelivered() {
		return OrderConfirmationType.valueOf(getOrderType()) == OrderConfirmationType.UNDELIVERED;
	}

	private void returnAll() {
		setDetails(bookedDetails.stream().map(d -> returnItems(d)).collect(Collectors.toList()));
	}

	private void setReason(OrderReturnType reason) {
		get().setReceivingModifiedBy(reason.toString());
	}

	private BillableDetail returnItems(BillableDetail d) {
		d.setReturnedQty(d.getInitialQty());
		return d;
	}
}
