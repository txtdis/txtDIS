package ph.txtdis.service;

import static java.math.BigDecimal.ONE;
import static org.apache.commons.lang3.StringUtils.isNumeric;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.splitByCharacterType;
import static ph.txtdis.type.BillingType.DELIVERY;
import static ph.txtdis.util.TextUtils.nullIfEmpty;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Customer;
import ph.txtdis.exception.AlreadyReferencedBookingIdException;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.exception.NotForDeliveryReportException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.type.BillableType;

public abstract class AbstractBillingService extends AbstractBillableService implements BillingService {

	@Autowired
	private TotaledBillableService totalService;

	@Autowired
	private VatService vatService;

	private BillableType type;

	private String prefix, idNo, suffix;

	@Override
	public String getAlternateName() {
		if (isAnInvoice())
			return "S/I";
		return "D/R";
	}

	protected boolean isAnInvoice() {
		return type == BillableType.INVOICE;
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
	public Billable getBilling(String prefix, Long id, String suffix) throws Exception {
		return findBillable("/find?prefix=" + prefix + "&id=" + id + "&suffix=" + suffix);
	}

	@Override
	public String getBillingPrompt() {
		if (isAnInvoice())
			return "Invoiced";
		return getAlternateName() + "'d";
	}

	@Override
	public String getHeaderText() {
		if (isAnInvoice())
			return "Sales Invoice";
		return "Delivery Report";
	}

	@Override
	public String getIdPrompt() {
		return getAlternateName() + " No.";
	}

	@Override
	public String getSpunModule() {
		if (isAnInvoice())
			return super.getSpunModule();
		return "deliveryReport";
	}

	@Override
	public List<Billable> listAged(Customer customer) {
		return getList("/aged?customer=" + customer.getId());
	}

	@Override
	public List<Billable> listAging(Customer customer) {
		return getList("/aging?customer=" + customer.getId());
	}

	@Override
	public void setType(BillableType type) {
		this.type = type;
	}

	@Override
	public void updateUponReferenceIdValidation(Long id) throws Exception {
		if (isNewAndOffSite())
			throw new NotAllowedOffSiteTransactionException();
		set(validateBooking(id.toString()));
		get().setBilledBy(credentialService.username());
		updateSummaries(getDetails());
	}

	protected Billable validateBooking(String id) throws Exception {
		Billable b = findReference(id);
		confirmBookingExists(id, b);
		confirmBookingHasBeenBilled(id, b);
		confirmBookingHasBeenPicked(id, b);
		confirmDeliveryReportingIsAllowed(b);
		return setOrderNo(b);
	}

	private void confirmDeliveryReportingIsAllowed(Billable b) throws Exception {
		if (!isAnInvoice() && !isForDR(b))
			throw new NotForDeliveryReportException(b.getCustomerName());
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

	private Billable findReference(String id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return findBillable("/booking?id=" + id);
	}

	private void confirmBookingHasBeenBilled(String id, Billable b) throws Exception {
		if (getBilledOn() != null)
			throw new AlreadyReferencedBookingIdException(id, b);
	}

	private Billable setOrderNo(Billable b) {
		b.setPrefix(getPrefix());
		b.setNumId(getNumId());
		b.setPrefix(getPrefix());
		return b;
	}

	@Override
	public void updateSummaries(List<BillableDetail> items) {
		set(totalService.updateTotals(get()));
	}

	@Override
	@SuppressWarnings("unchecked")
	public Billable find(String id) throws Exception {
		if (!isAnInvoice())
			return super.find(id);
		Billable b = findByThreePartId(id);
		if (b == null)
			throw new NotFoundException(getModuleId() + id);
		return b;
	}

	private Billable findByThreePartId(String id) throws Exception {
		setThreePartIdFromOrderNo(id);
		return getBilling(prefix, Long.valueOf(idNo), suffix);
	}

	private void setThreePartIdFromOrderNo(String id) throws Exception {
		String[] ids = split(id, "-");
		if (ids == null || ids.length == 0 || ids.length > 2)
			throw new NotFoundException(getModuleId() + id);
		if (ids.length == 1)
			setInvoiceIdWithoutPrefix(id, ids);
		else
			setInvoiceIdWithPrefix(id, ids);
	}

	private void setInvoiceIdWithoutPrefix(String orderNo, String[] ids) throws Exception {
		ids = splitByCharacterType(ids[0]);
		if (ids.length > 2)
			throw new NotFoundException(getModuleId() + orderNo);
		if (ids.length == 1)
			setInvoiceIdWithNumbersOnly(orderNo, ids[0]);
		else
			setInvoiceId("", ids[0], ids[1]);
	}

	private void setInvoiceIdWithNumbersOnly(String orderNo, String idNo) throws Exception {
		if (!isNumeric(idNo.replace("-", "")))
			throw new NotFoundException(getModuleId() + orderNo);
		setInvoiceId("", idNo, "");
	}

	private void setInvoiceId(String... ids) {
		prefix = ids[0];
		idNo = ids[1];
		suffix = ids[2];
	}

	private void setInvoiceIdWithPrefix(String orderNo, String[] ids) throws NotFoundException {
		String[] nos = splitByCharacterType(ids[1]);
		if (nos.length > 2)
			throw new NotFoundException(getModuleId() + orderNo);
		if (nos.length == 1)
			setInvoiceIdWithoutSuffix(orderNo, ids[0], nos[0]);
		else
			setInvoiceId(ids[0], nos[0], nos[1]);
	}

	private void setInvoiceIdWithoutSuffix(String orderNo, String code, String number) throws NotFoundException {
		if (!isNumeric(number))
			throw new NotFoundException(getModuleId() + orderNo);
		setInvoiceId(code, number, "");
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
	protected void nullifyAll() {
		super.nullifyAll();
		prefix = null;
		idNo = null;
		suffix = null;
	}

	@Override
	public void updateUponOrderNoValidation(String prefix, Long id, String suffix) throws Exception {
		if (isNewAndOffSite())
			throw new NotAllowedOffSiteTransactionException();
		checkforDuplicates(prefix, id, suffix);
		// verifyIdIsPartOfAnIssuedBookletImmediatelyPrecedingItsLast(prefix, id, suffix);
		setThreePartId(prefix, id, suffix);
	}

	private void checkforDuplicates(String prefix, Long id, String suffix) throws Exception {
		Billable i = getBilling(prefix, id, suffix);
		if (i != null)
			throw new DuplicateException(getModuleId() + id);
	}

	private void setThreePartId(String prefix, Long id, String suffix) {
		get().setPrefix(nullIfEmpty(prefix));
		get().setNumId(id);
		get().setSuffix(nullIfEmpty(suffix));
	}
}