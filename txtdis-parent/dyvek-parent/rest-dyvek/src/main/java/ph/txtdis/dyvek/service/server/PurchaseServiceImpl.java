package ph.txtdis.dyvek.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.domain.OrderDetailEntity;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.repository.PurchaseRepository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static java.time.LocalDate.now;
import static ph.txtdis.type.PartnerType.VENDOR;
import static ph.txtdis.util.NumberUtils.isPositive;
import static ph.txtdis.util.UserUtils.username;

@Service("purchaseService")
public class PurchaseServiceImpl //
	extends AbstractSpunSavedBillableService<PurchaseRepository> //
	implements PurchaseService {

	@Autowired
	private DeliveryService deliveryService;

	@Override
	protected List<BillableEntity> deliveries(BillableEntity e) {
		return deliveryService.findAllByVendorPurchaseNo(e.getCustomer().getId(), e.getOrderNo());
	}

	@Override
	public List<Billable> findAllOpenEndingInTheNext2Days() {
		LocalDate date = LocalDate.now().plusDays(2L);
		List<BillableEntity> l = findAllOpen(date);
		return toModels(l);
	}

	private List<BillableEntity> findAllOpen(LocalDate date) {
		return repository.findByDeliveryNullAndCustomerTypeAndOrderClosedOnNullAndOrderEndDateLessThan(VENDOR, date);
	}

	@Override
	public List<Billable> findAllOpen() {
		List<BillableEntity> l = findAllOpen(now().plusYears(1L));
		return toModels(l);
	}

	@Override
	public Billable findByPurchaseNo(String no) {
		BillableEntity b = repository.findByDeliveryNullAndCustomerTypeAndOrderNo(VENDOR, no);
		return toModel(b);
	}

	@Override
	public Billable toModel(BillableEntity e) {
		Billable b = super.toModel(e);
		if (b == null)
			return null;
		b.setEndDate(endDate(e));
		b.setPurchaseNo(e.getOrderNo());
		b.setPriceValue(priceValue(e));
		b.setBalanceQty(balanceQty(e));
		b.setDeliveries(details(e));
		b.setClosedBy(closedBy(e));
		b.setClosedOn(closedOn(e));
		return b;
	}

	private LocalDate endDate(BillableEntity e) {
		OrderDetailEntity d = e.getOrder();
		return d == null ? null : d.getEndDate();
	}

	private String closedBy(BillableEntity e) {
		OrderDetailEntity d = e.getOrder();
		return e == null ? null : d.getClosedBy();
	}

	private ZonedDateTime closedOn(BillableEntity e) {
		OrderDetailEntity d = e.getOrder();
		return e == null ? null : d.getClosedOn();
	}

	@Override
	protected BillableEntity firstEntity() {
		return repository.findFirstByDeliveryNullAndCustomerTypeOrderByIdAsc(VENDOR);
	}

	@Override
	protected BillableEntity nextEntity(Long id) {
		return repository.findFirstByDeliveryNullAndCustomerTypeAndIdGreaterThanOrderByIdAsc(VENDOR, id);
	}

	@Override
	protected BillableEntity lastEntity() {
		return repository.findFirstByDeliveryNullAndCustomerTypeOrderByIdDesc(VENDOR);
	}

	@Override
	protected BillableEntity previousEntity(Long id) {
		return repository.findFirstByDeliveryNullAndCustomerTypeAndIdLessThanOrderByIdDesc(VENDOR, id);
	}

	@Override
	public List<Billable> search(String po) {
		List<BillableEntity> l =
			repository.findByDeliveryNullAndCustomerTypeAndOrderNoNotNullAndOrderNoContainingIgnoreCase(VENDOR, po);
		return toModels(l);
	}

	@Override
	public BillableEntity toEntity(Billable b) {
		BillableEntity e = super.toEntity(b);
		if (e == null)
			return null;
		e.setOrderNo(b.getPurchaseNo());
		e.setCustomer(customer(b.getVendor()));
		return e;
	}

	@Override
	protected BillableEntity update(Billable b) {
		BillableEntity e = super.update(b);
		OrderDetailEntity oe = e.getOrder();
		if (oe.getClosedBy() == null && (b.getClosedBy() != null || !isPositive(b.getBalanceQty()))) {
			oe.setClosedBy(username());
			oe.setClosedOn(ZonedDateTime.now());
		}
		return e;
	}
}