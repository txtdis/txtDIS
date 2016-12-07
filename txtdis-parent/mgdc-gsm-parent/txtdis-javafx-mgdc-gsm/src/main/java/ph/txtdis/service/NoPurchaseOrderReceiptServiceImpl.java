package ph.txtdis.service;

import static ph.txtdis.util.NumberUtils.formatId;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.UnauthorizedUserException;
import ph.txtdis.info.Information;
import ph.txtdis.type.UserType;

@Service("purchaseReceiptService")
public class NoPurchaseOrderReceiptServiceImpl extends AbstractBillableService
		implements NoPurchaseOrderReceiptService {

	@Value("${vendor.id}")
	private String vendorId;

	@Override
	public String getAlternateName() {
		return "SIV";
	}

	@Override
	public Long getBookingId() {
		return get().getNumId();
	}

	@Override
	public String getHeaderText() {
		return "Purchase Receipt";
	}

	@Override
	public String getModuleIdNo() {
		return "" + getNumId();
	}

	@Override
	public LocalDate getOrderDate() {
		if (get().getOrderDate() == null)
			setOrderDate(today());
		return get().getOrderDate();
	}

	@Override
	public String getOrderNo() {
		return formatId(getBookingId());
	}

	@Override
	public String getReferencePrompt() {
		return "B/L No.";
	}

	@Override
	public Long getSpunId() {
		return isNew() ? null : getReceivingId();
	}

	@Override
	public String getSpunModule() {
		return "purchaseReceipt";
	}

	@Override
	public boolean isAppendable() {
		return isNew();
	}

	@Override
	public void save() throws Information, Exception {
		setReceivedByUser();
		super.save();
	}

	@Override
	protected BillableDetail setQty(BillableDetail sd) {
		sd.setReturnedQty(qty);
		return sd;
	}

	@Override
	public void updateUponReferenceIdValidation(Long id) throws Exception {
		verifyUserAuthorization();
		Billable b = findBillable("/find?prefix=&id=" + id + "&suffix=");
		if (b != null)
			throw new DuplicateException(getReferencePrompt() + " " + id);
		get().setNumId(id);
		get().setCustomerId(Long.valueOf(vendorId));
	}

	@Override
	protected void verifyUserAuthorization() throws UnauthorizedUserException {
		super.verifyUserAuthorization();
		if (!credentialService.isUser(UserType.STOCK_CHECKER) && !credentialService.isUser(UserType.STORE_KEEPER))
			throw new UnauthorizedUserException("Stock checkers or storekeepers only");
	}
}
