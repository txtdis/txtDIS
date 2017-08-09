package ph.txtdis.dyvek.service;

import static java.util.Arrays.asList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.EndDateBeforeStartException;
import ph.txtdis.exception.UnauthorizedUserException;

@Service("purchaseService")
public class PurchaseServiceImpl //
	extends AbstractOrderService<VendorService> //
	implements PurchaseService {

	@Override
	public List<Billable> findExpiringTheFollowingDayOrHaveExpired() {
		try {
			return getRestClientServiceForLists().module(getModuleName()).getList("/expiringOrExpiredInTheNext2Days");
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getModuleName() {
		return "purchaseOrder";
	}

	@Override
	public String getAlternateName() {
		return "P/O";
	}

	@Override
	public LocalDate getEndDate() {
		return get().getEndDate();
	}

	@Override
	public String getHeaderName() {
		return "Purchase Order";
	}

	@Override
	public String getOrderNo() {
		return get().getPurchaseNo();
	}

	@Override
	public List<String> listCustomers() {
		return isNew() ? customerService.listVendors() : asList(getCustomer());
	}

	@Override
	public String getCustomer() {
		return get().getVendor();
	}

	@Override
	public void setCustomer(String name) {
		get().setVendor(name);
	}

	@Override
	public void setEndDateUponValidation(LocalDate end) throws Exception {
		if (end.isBefore(getOrderDate()))
			throw new EndDateBeforeStartException();
		get().setEndDate(end);
	}

	@Override
	public void setOrderNoUponValidation(String no) throws Exception {
		if (!isManager())
			throw new UnauthorizedUserException("Buyers only.");
		Billable b = findByOrderNo(no);
		if (b != null)
			throw new DuplicateException("P/O No. " + no);
		get().setPurchaseNo(no);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Billable findByOrderNo(String no) throws Exception {
		return findBillable("/purchase?no=" + no);
	}

	@Override
	public void setQty(BigDecimal qty) {
		super.setQty(qty);
		get().setBalanceQty(qty);
	}
}
