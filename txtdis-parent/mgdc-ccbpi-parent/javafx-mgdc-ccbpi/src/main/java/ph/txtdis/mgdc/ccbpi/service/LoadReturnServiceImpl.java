package ph.txtdis.mgdc.ccbpi.service;

import static java.util.stream.Collectors.toList;
import static ph.txtdis.type.BeverageType.EMPTIES;
import static ph.txtdis.type.DeliveryType.PICK_UP;
import static ph.txtdis.util.UserUtils.username;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Booking;
import ph.txtdis.dto.PickList;
import ph.txtdis.dto.PickListDetail;
import ph.txtdis.dto.ReceivingDetail;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.ccbpi.dto.Item;
import ph.txtdis.mgdc.service.AbstractLoadingService;
import ph.txtdis.mgdc.service.PickListService;
import ph.txtdis.util.NumberUtils;

@Service("loadReturnService")
public class LoadReturnServiceImpl //
	extends AbstractLoadingService //
	implements LoadReturnService {

	@Autowired
	private BommedDiscountedPricedValidatedItemService itemService;

	@Autowired
	private OrderConfirmationService ocsService;

	@Autowired
	private PickListService pickListService;

	private Item item;

	private List<PickListDetail> pickedDetails;

	private PickListDetail receivingDetail;

	@Override
	public void acceptOnlyUpToBookedQty(BigDecimal qty) throws Exception {
		if (notEmpties())
			LoadReturnService.super.acceptOnlyUpToBookedQty(qty);
	}

	private boolean notEmpties() {
		return !getItem().getFamily().getName().equalsIgnoreCase(EMPTIES.toString());
	}

	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	public String getAlternateName() {
		return "Load-in";
	}

	@Override
	public String getCreatedBy() {
		return get().getReceivedBy();
	}

	@Override
	public ZonedDateTime getCreatedOn() {
		return get().getReceivedOn();
	}

	@Override
	public BommedDiscountedPricedValidatedItemService getItemService() {
		return itemService;
	}

	@Override
	public String getModuleName() {
		return "loadReturn";
	}

	@Override
	@SuppressWarnings("unchecked")
	public PickListDetail getReceivingDetail() {
		return receivingDetail;
	}

	@Override
	public void setReceivingDetail(ReceivingDetail detail) {
		receivingDetail = (PickListDetail) detail;
	}

	@Override
	public String getTruck() {
		String truck = get().getTruck();
		return truck == null && !isNew() ? PICK_UP.toString() : truck;
	}

	@Override
	public boolean isNew() {
		return get().getReceivedOn() == null;
	}

	@Override
	public boolean isAppendable() {
		return isNew();
	}

	@Override
	public List<String> listReceivableItemNames() {
		return pickedDetails.stream() //
			.map(d -> d.getItemName()) //
			.filter(n -> nonReturnedItem(n)).distinct() //
			.collect(toList());
	}

	@Override
	public boolean nonReturnedItem(String name) {
		return getDetails() == null ? true //
			: !getDetails().stream().anyMatch(d -> isItemReturned(name, d));
	}

	@Override
	public List<PickListDetail> getDetails() {
		List<PickListDetail> details = get().getDetails();
		return details == null ? null
			: details.stream() //
			.filter(d -> d != null && NumberUtils.isPositive(d.getReturnedQty())) //
			.collect(Collectors.toList());
	}

	private boolean isItemReturned(String name, PickListDetail d) {
		return !NumberUtils.isZero(d.getReturnedQty()) && name.equalsIgnoreCase(d.getItemName());
	}

	@Override
	public void setDetails(List<PickListDetail> l) {
		get().setDetails(l);
	}

	@Override
	public void reset() {
		super.reset();
		setItem(null);
		setReceivingDetail(null);
		pickedDetails = null;
	}

	@Override
	public void save() throws Information, Exception {
		get().setReceivedBy(username());
		LoadReturnService.super.save();
	}

	@Override
	public void returnAllPickedItemsIfNoneOfItsOCSHasAnRR() throws Exception {
		verifyNoOCSHasAnRR();
		get().setBookings(null);
		setDetails(returnAll());
	}

	private void verifyNoOCSHasAnRR() throws Exception {
		List<Billable> l = getBookings().stream().map(b -> toBillable(b)).collect(Collectors.toList());
		Billable ocs = l.stream().filter(b -> b.getReceivedOn() != null).findFirst().orElse(null);
		if (ocs != null)
			throw new InvalidException("Cannot do a full return for " + getAbbreviatedModuleNoPrompt() + getId() //
				+ "\nsince an R/R exists for its picked OCS numbered " + ocsNo(ocs));
	}

	private List<PickListDetail> returnAll() {
		return getPickedDetails().stream().map(d -> setReturnedQty(d)).collect(Collectors.toList());
	}

	private List<Booking> getBookings() {
		return get().getBookings();
	}

	private Billable toBillable(Booking b) {
		try {
			return ocsService.findById(b.getId());
		} catch (Exception e) {
			return new Billable();
		}
	}

	private String ocsNo(Billable ocs) {
		return getBookings().stream() //
			.filter(b -> b.getId().equals(ocs.getId())) //
			.findFirst().orElse(new Booking()).getLocation();
	}

	@Override
	public List<PickListDetail> getPickedDetails() {
		return pickedDetails;
	}

	private PickListDetail setReturnedQty(PickListDetail d) {
		d.setReturnedQty(d.getInitialQty());
		return d;
	}

	@Override
	public void setRemarks(String text) {
		get().setRemarks(text);
	}

	@Override
	public void updateUponPickListIdValidation(Long id) throws Exception {
		if (isNew())
			updateAfterValidatingPickListId(id);
	}

	private void updateAfterValidatingPickListId(Long id) throws Exception {
		PickList p = pickListService.findById(id);
		if (p.getReceivedBy() != null)
			throw new DuplicateException("Load Return for\nPickList No. " + id);
		setLoadReturn(p);
	}

	private void setLoadReturn(PickList p) {
		pickedDetails = p.getDetails();
		p.setDetails(null);
		set(p);
	}
}
