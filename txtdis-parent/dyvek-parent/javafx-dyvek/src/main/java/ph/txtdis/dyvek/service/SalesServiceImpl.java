package ph.txtdis.dyvek.service;

import static java.math.BigDecimal.ONE;
import static java.util.Arrays.asList;
import static ph.txtdis.util.NumberUtils.nullIfZero;
import static ph.txtdis.util.NumberUtils.toPercentRate;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.UnauthorizedUserException;
import ph.txtdis.util.NumberUtils;

@Service("salesService")
public class SalesServiceImpl //
	extends AbstractOrderService<VendorService> //
	implements SalesService {

	@Autowired
	private TradingClientService clientService;

	@Override
	public String getAlternateName() {
		return "S/O";
	}

	@Override
	public String getHeaderName() {
		return "Sales Order";
	}

	@Override
	public String getOrderNo() {
		return get().getSalesNo();
	}

	@Override
	public String getModuleName() {
		return "salesOrder";
	}

	@Override
	public List<String> listCustomers() {
		return isNew() ? clientService.listClients() : asList(get().getClient());
	}

	@Override
	public void setOrderNoUponValidation(String no) throws Exception {
		if (!isManager())
			throw new UnauthorizedUserException("Sellers only.");
		Billable b = findByOrderNo(no);
		if (b != null)
			throw new DuplicateException("P/O No. " + no);
		get().setSalesNo(no);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Billable findByOrderNo(String no) throws Exception {
		return findBillable("/sales?no=" + no + "&of=" + getCustomer());
	}

	@Override
	public String getCustomer() {
		return get().getClient();
	}

	@Override
	public void setCustomer(String name) {
		get().setClient(name);
	}

	@Override
	public void setQty(BigDecimal qty) {
		super.setQty(qty);
		get().setBalanceQty(qtyPlusTolerance());
	}

	private BigDecimal qtyPlusTolerance() {
		BigDecimal tolerance = ONE.add(toPercentRate(getTolerancePercent()));
		return getQty().multiply(tolerance);
	}

	@Override
	public BigDecimal getTolerancePercent() {
		return NumberUtils.zeroIfNull(get().getTolerancePercent());
	}

	@Override
	public void setTolerancePercent(BigDecimal percent) {
		get().setTolerancePercent(nullIfZero(percent));
	}
}
