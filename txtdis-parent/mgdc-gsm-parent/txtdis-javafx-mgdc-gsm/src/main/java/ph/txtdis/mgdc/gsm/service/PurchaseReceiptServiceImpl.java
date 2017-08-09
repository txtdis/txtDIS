package ph.txtdis.mgdc.gsm.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.UnauthorizedUserException;
import ph.txtdis.info.Information;

import java.time.LocalDate;

import static ph.txtdis.type.UserType.*;
import static ph.txtdis.util.NumberUtils.toIdText;
import static ph.txtdis.util.UserUtils.isUser;

@Service("purchaseReceiptService")
public class PurchaseReceiptServiceImpl //
	extends AbstractBillableService //
	implements NoPurchaseOrderReceiptService {

	@Value("${vendor.id}")
	private String vendorId;

	@Override
	public String getAlternateName() {
		return "SIV";
	}

	@Override
	public String getHeaderName() {
		return "Purchase Receipt";
	}

	@Override
	public String getModuleNo() {
		return "" + get().getNumId();
	}

	@Override
	public LocalDate getOrderDate() {
		if (get().getOrderDate() == null)
			setOrderDate(today());
		return get().getOrderDate();
	}

	@Override
	public String getOrderNo() {
		return toIdText(getBookingId());
	}

	@Override
	public Long getBookingId() {
		return get().getNumId();
	}

	@Override
	public String getModuleName() {
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
		Billable b = (Billable) findByModuleId(id);
		if (b != null)
			throw new DuplicateException(getReferencePrompt() + " " + id);
		get().setNumId(id);
		get().setCustomerId(Long.valueOf(vendorId));
	}

	private void verifyUserAuthorization() throws Exception {
		if (isUser(MANAGER))
			return;
		if (!isUser(STOCK_CHECKER) && !isUser(STORE_KEEPER))
			throw new UnauthorizedUserException("Stock checkers or keepers only");
	}

	@Override
	public String getReferencePrompt() {
		return "B/L No.";
	}
}
