package ph.txtdis.service;

import static ph.txtdis.type.UserType.MANAGER;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.EntityDecisionNeeded;
import ph.txtdis.dto.Item;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.exception.ToBeReturnedItemNotPurchasedWithinTheLastSixMonthException;
import ph.txtdis.info.Information;
import ph.txtdis.type.ScriptType;

public abstract class AbstractRmaReplacementService //
		extends AbstractBillableService //
		implements RmaReplacementService {

	@Autowired
	private CustomerValidationService customerValidationService;

	@Autowired
	private ScriptService scriptService;

	@Override
	public boolean canApprove() {
		return !isNew() && credentialService.isUser(MANAGER);
	}

	@Override
	public String getAlternateName() {
		return "RMA";
	}

	@Override
	public String getHeaderText() {
		return getAlternateName();
	}

	@Override
	public String getModuleIdNo() {
		return getBookingId().toString();
	}

	@Override
	public LocalDate getOrderDate() {
		if (get().getOrderDate() == null)
			setOrderDate(syncService.getServerDate());
		return get().getOrderDate();
	}

	@Override
	public String getReplacedBy() {
		return get().getPickedBy();
	}

	@Override
	public ZonedDateTime getReplacedOn() {
		return get().getPickedOn();
	}

	@Override
	public Long getReplacementId() {
		return get().getPickListId();
	}

	@Override
	public ScriptService getScriptService() {
		return scriptService;
	}

	@Override
	public <T extends EntityDecisionNeeded<Long>> ScriptType getScriptType(T d) {
		return ScriptType.BILLING_APPROVAL;
	}

	@Override
	public boolean isAppendable() {
		return isNew();
	}

	@Override
	public boolean isReturnValid() {
		return getIsValid() != null && getIsValid() == true;
	}

	@Override
	public void save() throws Information, Exception {
		setReceivedByUser();
		super.save();
	}

	@Override
	public void saveItemReturnReceiptData() throws Exception {
		if (isNewAndOffSite())
			throw new NotAllowedOffSiteTransactionException();
		setReceivedByUser();
	}

	@Override
	public void searchForCustomer(String name) {
		try {
			customerService.search(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateUponCustomerIdValidation(Long id) throws Exception {
		customerValidationService.validate(id, getOrderDate());
	}

	@Override
	public String username() {
		return credentialService.username();
	}

	@Override
	public Item verifyItem(Long id) throws Exception {
		Item i = super.verifyItem(id);
		confirmItemTobeReturnedHasBeenPurchasedBefore(i);
		return i;
	}

	private void confirmItemTobeReturnedHasBeenPurchasedBefore(Item i) throws Exception {
		Billable b = findBillable("/purchased?by=" + getCustomerId() + "&item=" + i.getId());
		if (b == null)
			throw new ToBeReturnedItemNotPurchasedWithinTheLastSixMonthException(i, getCustomerName());
	}
}
