package ph.txtdis.dyvek.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.domain.CustomerEntity;
import ph.txtdis.dyvek.domain.ItemEntity;
import ph.txtdis.dyvek.domain.OrderDetailEntity;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.model.BillableDetail;
import ph.txtdis.repository.SpunRepository;
import ph.txtdis.service.AbstractSpunSavedKeyedService;
import ph.txtdis.service.ServerUserService;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.NumberUtils.isZero;
import static ph.txtdis.util.NumberUtils.zeroIfNull;

public abstract class AbstractSpunSavedBillableService<R extends SpunRepository<BillableEntity, Long>> //
	extends AbstractSpunSavedKeyedService<R, BillableEntity, Billable, Long> {

	@Autowired
	protected CustomerService customerService;

	@Autowired
	protected ItemService itemService;

	@Autowired
	protected ServerUserService userService;

	@Override
	public Billable toModel(BillableEntity e) {
		if (e == null)
			return null;
		Billable b = toBillable(e);
		b.setTotalQty(totalQty(e));
		b.setPriceValue(priceValue(e));
		b.setTotalValue(totalValue(e));
		b.setRemarks(e.getRemarks());
		b.setDeliveries(details(e));
		return setCreationData(b, e);
	}

	protected Billable toBillable(BillableEntity e) {
		Billable b = new Billable();
		b.setId(e.getId());
		b.setOrderDate(e.getOrderDate());
		b.setVendor(customer(e));
		b.setItem(item(e));
		return b;
	}

	private BigDecimal totalQty(BillableEntity e) {
		BigDecimal qty = e.getTotalQty();
		return qty == null ? ZERO : qty;
	}

	protected BigDecimal priceValue(BillableEntity b) {
		try {
			return b.getOrder().getPriceValue();
		} catch (Exception e) {
			return ZERO;
		}
	}

	protected BigDecimal totalValue(BillableEntity e) {
		return totalQty(e).multiply(priceValue(e));
	}

	protected List<BillableDetail> details(BillableEntity e) {
		List<BillableEntity> l = deliveries(e);
		return l == null ? emptyList() : l.stream().map(b -> toDetail(b)).collect(toList());
	}

	protected Billable setCreationData(Billable b, BillableEntity e) {
		b.setCreatedBy(e.getCreatedBy());
		b.setCreatedOn(e.getCreatedOn());
		return b;
	}

	protected String customer(BillableEntity e) {
		CustomerEntity c = e.getCustomer();
		return c == null ? null : c.getName();
	}

	protected String item(BillableEntity e) {
		ItemEntity i = e.getItem();
		return i == null ? null : i.getName();
	}

	protected abstract List<BillableEntity> deliveries(BillableEntity e);

	protected BillableDetail toDetail(BillableEntity e) {
		BillableDetail d = idAndDateAndNoOnlyDetail(e);
		d.setCustomer(customer(e));
		d.setItem(item(e));
		d.setPriceValue(priceValue(e));
		d.setQty(e.getTotalQty());
		return d;
	}

	protected BillableDetail idAndDateAndNoOnlyDetail(BillableEntity e) {
		BillableDetail d = new BillableDetail();
		d.setId(e.getId());
		d.setOrderDate(e.getOrderDate());
		d.setOrderNo(e.getOrderNo());
		return d;
	}

	@Override
	public BillableEntity toEntity(Billable b) {
		if (b == null)
			return null;
		return b.getId() == null ? createEntity(b) : update(b);
	}

	private BillableEntity createEntity(Billable b) {
		BillableEntity e = new BillableEntity();
		e.setOrderDate(b.getOrderDate());
		e.setOrder(order(b));
		e.setItem(item(b));
		e.setTotalQty(b.getTotalQty());
		e.setRemarks(b.getRemarks());
		return e;
	}

	protected BillableEntity update(Billable b) {
		BillableEntity e = repository.findOne(b.getId());
		e.setRemarks(b.getRemarks());
		return e;
	}

	protected OrderDetailEntity order(Billable b) {
		BigDecimal price = b.getPriceValue();
		if (isZero(price))
			return null;
		OrderDetailEntity e = new OrderDetailEntity();
		e.setPriceValue(price);
		e.setEndDate(b.getEndDate());
		return e;
	}

	protected ItemEntity item(Billable b) {
		String name = b.getItem();
		return itemService.findEntityByName(name);
	}

	protected CustomerEntity customer(String name) {
		return customerService.findEntityByName(name);
	}

	protected BigDecimal balanceQty(BillableEntity b) {
		return zeroIfNull(b.getTotalQty()).subtract(deliveredQty(b));
	}

	protected BigDecimal deliveredQty(BillableEntity b) {
		try {
			return deliveries(b).stream() //
				.flatMap(d -> d.getReferences().stream()) //
				.filter(d -> d.getReference().getCustomer().equals(b.getCustomer()) &&
					d.getReference().getOrderNo().equals(b.getOrderNo()))
				.map(d -> d.getQty()) //
				.reduce(ZERO, BigDecimal::add);
		} catch (Exception e) {
			e.printStackTrace();
			return ZERO;
		}
	}
}