package ph.txtdis.mgdc.ccbpi.service;

import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.mgdc.ccbpi.dto.Item;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static ph.txtdis.util.DateTimeUtils.toOrderConfirmationDate;

public abstract class AbstractCokeBillableService //
	extends AbstractBillableService //
	implements ShippedBillableService {

	protected static final String CUSTOMER_ID = "123456789";

	protected static final String DATE = "7/8/09";

	protected static final String ORDER_DATE = "20090708";

	@Autowired
	protected BommedDiscountedPricedValidatedItemService itemService;

	@Override
	public Item confirmItemExistsAndIsNotDeactivated(Long id) throws Exception {
		return getItemService().findByVendorNo(id.toString());
	}

	@Override
	public String getOpenDialogPrompt() {
		return "Enter " + getShipmentPrompt();
	}

	@Override
	public String getPostedTitleText() {
		return getSavingInfo();
	}

	@Override
	public String getSavingInfo() {
		return getAbbreviatedModuleNoPrompt() + getBookingId();
	}

	@Override
	public String getRoute() {
		return get().getSuffix();
	}

	@Override
	public List<BigDecimal> getTotals(List<BillableDetail> l) {
		return l == null ? null : asList(totalQty(l), totalValue(l));
	}

	private BigDecimal totalQty(List<BillableDetail> l) {
		return l.stream().map(BillableDetail::getFinalQtyInCases).reduce(ZERO, BigDecimal::add);
	}

	private BigDecimal totalValue(List<BillableDetail> l) {
		return l.stream().map(BillableDetail::getFinalSubtotalValue).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	@Override
	public boolean isAppendable() {
		return isNew();
	}

	protected String route() {
		return get().getSuffix();
	}

	protected String orderConfirmationNo(Billable b) {
		return customerVendorId(b) + "-" + toOrderConfirmationDate(b.getOrderDate()) + "/" + sequenceNo(b);
	}

	private String customerVendorId(Billable b) {
		Long id = b.getCustomerVendorId();
		return id == null ? CUSTOMER_ID : id.toString();
	}

	private Long sequenceNo(Billable b) {
		Long id = b.getBookingId();
		return id == null ? 1L : id;
	}

	protected Billable throwNotFoundExceptionIfNull(Billable b, String id) throws Exception {
		if (b == null)
			throw new NotFoundException(getAbbreviatedModuleNoPrompt() + id);
		return b;
	}
}
