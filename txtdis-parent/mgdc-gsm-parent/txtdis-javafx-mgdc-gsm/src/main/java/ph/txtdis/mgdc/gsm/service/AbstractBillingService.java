package ph.txtdis.mgdc.gsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.exception.AlreadyReferencedBookingIdException;
import ph.txtdis.exception.NotForDeliveryReportException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.service.TotaledBillableService;
import ph.txtdis.type.ModuleType;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ONE;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.*;
import static ph.txtdis.type.BillingType.DELIVERY;
import static ph.txtdis.util.NumberUtils.isPositive;

public abstract class AbstractBillingService //
	extends AbstractBillableService //
	implements BillingService {

	protected ModuleType type;

	@Autowired
	private TotaledBillableService totalService;

	@Autowired
	private VatService vatService;

	@Override
	@SuppressWarnings("unchecked")
	public Billable findByModuleKey(String key) throws Exception {
		return findByThreePartKey(key);
	}

	private Billable findByThreePartKey(String key) throws Exception {
		String[] keys = threePartKeyFromOrderNo(key);
		return checkPresence(getRestClientService(), keys[0], toId(keys[1]), keys[2]);
	}

	private String[] threePartKeyFromOrderNo(String key) throws Exception {
		String[] keys = split(key, "-");
		if (keys == null || keys.length == 0 || keys.length > 2)
			throw new NotFoundException(getAbbreviatedModuleNoPrompt() + key);
		return keys.length == 1 ? invoiceNoWithoutPrefix(key, keys) : invoiceNoWithPrefix(key, keys);
	}

	private Long toId(String key) {
		Long id = Long.valueOf(key);
		return isAnInvoice() ? id : -id;
	}

	private String[] invoiceNoWithoutPrefix(String orderNo, String[] keys) throws Exception {
		keys = splitByCharacterType(keys[0]);
		if (keys.length > 2)
			throw new NotFoundException(getAbbreviatedModuleNoPrompt() + orderNo);
		return keys.length == 1 ? invoiceNoWithNumbersOnly(orderNo, keys[0]) : new String[]{"", keys[0], keys[1]};
	}

	private String[] invoiceNoWithPrefix(String orderNo, String[] keys) throws NotFoundException {
		String[] nos = splitByCharacterType(keys[1]);
		if (nos.length > 2)
			throw new NotFoundException(getAbbreviatedModuleNoPrompt() + orderNo);
		return nos.length == 1 ? invoiceNoWithoutSuffix(orderNo, keys[0], nos[0]) : new String[]{keys[0], nos[0],
			nos[1]};
	}

	@Override
	public boolean isAnInvoice() {
		return type == ModuleType.INVOICE;
	}

	private String[] invoiceNoWithNumbersOnly(String orderNo, String idNo) throws Exception {
		if (!isNumeric(idNo.replace("-", "")))
			throw new NotFoundException(getAbbreviatedModuleNoPrompt() + orderNo);
		return new String[]{"", idNo, ""};
	}

	private String[] invoiceNoWithoutSuffix(String orderNo, String code, String idNo) throws NotFoundException {
		if (!isNumeric(idNo))
			throw new NotFoundException(getAbbreviatedModuleNoPrompt() + orderNo);
		return new String[]{code, idNo, ""};
	}

	@Override
	public Billable findBilling(String prefix, Long id, String suffix) throws Exception {
		return findBilling(getRestClientService(), prefix, id, suffix);
	}

	@Override
	public BigDecimal getBalance() {
		try {
			BigDecimal d = get().getUnpaidValue().abs().subtract(ONE);
			return d.compareTo(ONE) <= 0 ? null : d;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getBillingPrompt() {
		return getAlternateName() + "'d";
	}

	@Override
	public String getAlternateName() {
		return isAnInvoice() ? "S/I" : "D/R";
	}

	@Override
	public String getHeaderName() {
		return isAnInvoice() ? "Sales Invoice" : super.getHeaderName();
	}

	@Override
	public String getIdPrompt() {
		return getAlternateName() + " No.";
	}

	@Override
	public String getModuleName() {
		return isAnInvoice() ? "invoice" : "deliveryReport";
	}

	@Override
	public String getModuleNo() {
		return getOrderNo();
	}

	@Override
	public String getOrderNo() {
		return get().getOrderNo().replace("(", "").replace(")", "");
	}

	@Override
	public BigDecimal getVat() {
		return vatService.getVat(getTotalValue());
	}

	@Override
	public BigDecimal getVatable() {
		return vatService.getVatable(getTotalValue());
	}

	@Override
	public List<Billable> listAged(Customer customer) {
		return findBillables("/aged?customer=" + customer.getId());
	}

	@Override
	public List<Billable> listAging(Customer customer) {
		return findBillables("/aging?customer=" + customer.getId());
	}

	@Override
	public void setType(ModuleType type) {
		this.type = type;
	}

	@Override
	public void updateUponReferenceIdValidation(Long id) throws Exception {
		set(validateBooking(id.toString()));
		get().setBilledBy(getUsername());
		updateSummaries(getDetails());
	}

	protected Billable validateBooking(String id) throws Exception {
		Billable b = findReference(id);
		confirmBookingExists(id, b);
		confirmBookingHasNotBeenBilled(id, b);
		confirmBookingHasBeenPicked(id, b);
		confirmDeliveryReportingIsAllowed(b);
		return setOrderNo(b);
	}

	@Override
	public void updateSummaries(List<BillableDetail> items) {
		super.updateSummaries(items);
		updateTotals();
	}

	@Override
	public List<BillableDetail> getDetails() {
		try {
			return get().getDetails().stream().filter(d -> isPositive(d.getFinalQty())).collect(toList());
		} catch (Exception e) {
			return emptyList();
		}
	}

	private Billable findReference(String id) throws Exception {
		return findBillable("/booking?id=" + id);
	}

	private void confirmBookingHasNotBeenBilled(String id, Billable b) throws Exception {
		if (b.getBilledOn() != null)
			throw new AlreadyReferencedBookingIdException(id, b.getOrderNo());
	}

	private void confirmDeliveryReportingIsAllowed(Billable b) throws Exception {
		if (!isAnInvoice() && !isForDR(b))
			throw new NotForDeliveryReportException(b.getCustomerName());
	}

	protected Billable setOrderNo(Billable b) {
		b.setPrefix(getPrefix());
		b.setNumId(getNumId());
		b.setSuffix(getSuffix());
		return b;
	}

	protected void updateTotals() {
		if (isNew())
			set(totalService.updateFinalTotals(get()));
	}

	protected boolean isForDR(Billable b) {
		try {
			return customerService.getBillingType(b) == DELIVERY ? true : returnedAllItems(b);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean returnedAllItems(Billable b) {
		if (b.getReceivedOn() == null)
			return false;
		return !b.getDetails().stream().anyMatch(d -> notReturnedAll(d));
	}

	private boolean notReturnedAll(BillableDetail d) {
		try {
			return d.getInitialQty().compareTo(d.getReturnedQty()) > 0;
		} catch (Exception e) {
			return true;
		}
	}

	@Override
	public void save() throws Information, Exception {
		get().setBilledBy(getUsername());
		super.save();
	}

	@Override
	public void updateUponOrderNoValidation(String prefix, Long id, String suffix) throws Exception {
		checkNoDuplicates(getRestClientService(), prefix, id, suffix);
		// verifyIdIsPartOfAnIssuedBookletImmediatelyPrecedingItsLast(prefix, id, suffix);
		setThreePartId(prefix, id, suffix);
	}
}