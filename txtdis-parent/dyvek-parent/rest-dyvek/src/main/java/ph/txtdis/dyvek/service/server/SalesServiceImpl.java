package ph.txtdis.dyvek.service.server;

import org.springframework.stereotype.Service;
import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.domain.CustomerEntity;
import ph.txtdis.dyvek.domain.OrderDetailEntity;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.repository.SalesRepository;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static ph.txtdis.type.PartnerType.OUTLET;
import static ph.txtdis.util.NumberUtils.toPercentRate;
import static ph.txtdis.util.NumberUtils.zeroIfNull;

@Service("salesService")
public class SalesServiceImpl //
	extends AbstractSpunSavedBillableService<SalesRepository> //
	implements SalesService {

	@Override
	protected List<BillableEntity> deliveries(BillableEntity e) {
		String orderNo = e.getOrderNo();
		CustomerEntity c = e.getCustomer();
		return orderNo == null || c == null ? null : //
			repository.findByReferencesReferenceCustomerAndReferencesReferenceOrderNo(c, orderNo);
	}

	@Override
	public List<Billable> findAllOpen() {
		List<BillableEntity> l = repository.findByCustomerTypeAndOrderClosedOnNullAndDeliveryNull(OUTLET);
		return toModels(l);
	}

	@Override
	public Billable findBySalesNo(String client, String salesNo) {
		BillableEntity b = repository.findByDeliveryNullAndCustomerNameAndOrderNo(client, salesNo);
		return toModel(b);
	}

	@Override
	public Billable toModel(BillableEntity e) {
		Billable b = super.toModel(e);
		if (b == null)
			return null;
		b.setSalesNo(e.getOrderNo());
		b.setVendor(null);
		b.setClient(customer(e));
		b.setPriceValue(priceValue(e));
		b.setBalanceQty(balanceQty(e));
		b.setDeliveries(details(e));
		b.setTolerancePercent(tolerancePercent(e));
		return b;
	}

	@Override
	protected BigDecimal balanceQty(BillableEntity b) {
		return (zeroIfNull(b.getTotalQty()) //
			.multiply(tolerance(b))) //
			.subtract(deliveredQty(b));
	}

	private BigDecimal tolerancePercent(BillableEntity b) {
		try {
			return b.getOrder().getTolerancePercent();
		} catch (Exception e) {
			return ZERO;
		}
	}

	private BigDecimal tolerance(BillableEntity b) {
		try {
			return ONE.add(toPercentRate(b.getOrder().getTolerancePercent()));
		} catch (Exception e) {
			return ONE;
		}
	}

	@Override
	protected BillableEntity firstEntity() {
		return repository.findFirstByCustomerTypeOrderByIdAsc(OUTLET);
	}

	@Override
	protected BillableEntity lastEntity() {
		return repository.findFirstByCustomerTypeOrderByIdDesc(OUTLET);
	}

	@Override
	protected BillableEntity nextEntity(Long id) {
		return repository.findFirstByCustomerTypeAndIdGreaterThanOrderByIdAsc(OUTLET, id);
	}

	@Override
	protected OrderDetailEntity order(Billable b) {
		OrderDetailEntity e = super.order(b);
		if (e == null)
			return null;
		e.setTolerancePercent(b.getTolerancePercent());
		return e;
	}

	@Override
	protected BillableEntity previousEntity(Long id) {
		return repository.findFirstByCustomerTypeAndIdLessThanOrderByIdDesc(OUTLET, id);
	}

	@Override
	public List<Billable> search(String so) {
		List<BillableEntity> l = repository.findByCustomerTypeAndOrderNoNotNullAndOrderNoContainingIgnoreCase(OUTLET,
			so);
		return toModels(l);
	}

	@Override
	public BillableEntity toEntity(Billable b) {
		BillableEntity e = super.toEntity(b);
		if (e == null)
			return null;
		e.setOrderNo(b.getSalesNo());
		e.setCustomer(customer(b.getClient()));
		return e;
	}
}