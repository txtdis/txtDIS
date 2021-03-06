package ph.txtdis.mgdc.gsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.exception.ToBeReturnedItemNotPurchasedWithinTheLastSixMonthException;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.mgdc.service.TotaledBillableService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.util.DateTimeUtils.getServerDate;
import static ph.txtdis.util.UserUtils.isUser;

public abstract class AbstractRmaService //
	extends AbstractBillableService //
	implements RmaService {

	@Autowired
	private TotaledBillableService totalService;

	@Override
	public boolean canApprove() {
		return !isNew() && isUser(MANAGER);
	}

	@Override
	public BillableDetail createDetail() {
		BillableDetail d = super.createDetail();
		d.setPriceValue(item.getLatestPrimarySellingUnitPrice().negate());
		return d;
	}

	@Override
	public String getHeaderName() {
		return getAlternateName();
	}

	@Override
	public String getAlternateName() {
		return "RMA";
	}

	@Override
	public String getModuleNo() {
		return getBookingId().toString();
	}

	@Override
	public LocalDate getOrderDate() {
		if (get().getOrderDate() == null)
			setOrderDate(getServerDate());
		return get().getOrderDate();
	}

	@Override
	public String getSavingInfo() {
		if (get().getBilledOn() != null)
			return getAlternateName() + " S/I No. " + get().getOrderNo();
		else if (get().getReceivedOn() != null)
			return getAlternateName() + " R/R No. " + getReceivingId();
		return super.getSavingInfo();

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
	public void saveReturnReceiptData() throws Information, Exception {
		setReceivedByUser();
		save();
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
	public void setCustomerData(Customer c) {
		get().setCustomerId(c.getId());
		get().setCustomerName(c.getName());
		get().setCustomerAddress(c.getAddress());
	}

	@Override
	public void updateSummaries(List<BillableDetail> items) {
		super.updateSummaries(items);
		set(totalService.updateInitialTotals(get()));
	}

	@Override
	public Item verifyItem(Long id) throws Exception {
		Item i = super.verifyItem(id);
		BigDecimal price = getPriceOfItemTobeReturnedFromItsLastPurchase(i);
		i.setLatestPrimarySellingUnitPrice(price);
		return i;
	}

	private BigDecimal getPriceOfItemTobeReturnedFromItsLastPurchase(Item i) throws Exception {
		try {
			Billable b = findBillable("/purchased?by=" + getCustomerId() + "&item=" + i.getId());
			return b.getDetails().stream().filter(d -> d.getId().equals(i.getId())).findFirst().get().getPriceValue();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ToBeReturnedItemNotPurchasedWithinTheLastSixMonthException(i.getName(), getCustomerName());
		}
	}
}
