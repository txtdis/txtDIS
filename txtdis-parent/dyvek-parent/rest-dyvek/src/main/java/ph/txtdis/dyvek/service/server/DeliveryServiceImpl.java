package ph.txtdis.dyvek.service.server;

import org.springframework.stereotype.Service;
import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.domain.DeliveryDetailEntity;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.repository.DeliveryRepository;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Collections.emptyList;

@Service("deliveryService")
public class DeliveryServiceImpl //
	extends AbstractSpunSavedBillableService<DeliveryRepository> //
	implements DeliveryService {

	@Override
	public List<BillableEntity> findAllByVendorPurchaseNo(Long vendorId, String po) {
		return po == null ? emptyList() :
			repository.findByDeliveryNotNullAndReferencesReferenceCustomerIdAndReferencesReferenceOrderNo(vendorId, po);
	}

	@Override
	public Billable findByVendorDeliveryNo(String vendor, String dr) {
		BillableEntity e = repository.findByDeliveryNotNullAndCustomerNameAndOrderNo(vendor, dr);
		return toModel(e);
	}

	@Override
	public Billable toModel(BillableEntity e) {
		Billable b = super.toModel(e);
		if (b == null)
			return null;
		b.setDeliveryNo(e.getOrderNo());
		b.setClient(client(e));
		b.setTruckPlateNo(truckPlateNo(e));
		b.setTruckScaleNo(truckScaleNo(e));
		b.setGrossWeight(grossWeight(e));
		b.setFfaPercent(ffa(e));
		b.setIodineQty(iodineValue(e));
		b.setColor(color(e));
		return b;
	}

	private String client(BillableEntity e) {
		DeliveryDetailEntity d = e.getDelivery();
		return d == null || d.getRecipient() == null ? null : d.getRecipient().getName();
	}

	private String truckPlateNo(BillableEntity e) {
		DeliveryDetailEntity d = e.getDelivery();
		return d == null ? null : d.getPlateNo();
	}

	private String truckScaleNo(BillableEntity e) {
		DeliveryDetailEntity d = e.getDelivery();
		return d == null ? null : d.getScaleNo();
	}

	private BigDecimal grossWeight(BillableEntity e) {
		DeliveryDetailEntity d = e.getDelivery();
		return d == null ? null : d.getGrossWeight();
	}

	private BigDecimal ffa(BillableEntity e) {
		DeliveryDetailEntity d = e.getDelivery();
		return d == null ? null : d.getFfa();
	}

	private BigDecimal iodineValue(BillableEntity e) {
		DeliveryDetailEntity d = e.getDelivery();
		return d == null ? null : d.getIodine();
	}

	private String color(BillableEntity e) {
		DeliveryDetailEntity d = e.getDelivery();
		return d == null ? null : d.getColor();
	}

	@Override
	protected BillableEntity firstEntity() {
		return repository.findFirstByDeliveryNotNullOrderByIdAsc();
	}

	@Override
	protected BillableEntity lastEntity() {
		return repository.findFirstByDeliveryNotNullOrderByIdDesc();
	}

	@Override
	protected BillableEntity nextEntity(Long id) {
		return repository.findFirstByDeliveryNotNullAndIdGreaterThanOrderByIdAsc(id);
	}

	@Override
	protected BillableEntity previousEntity(Long id) {
		return repository.findFirstByDeliveryNotNullAndIdLessThanOrderByIdDesc(id);
	}

	@Override
	public List<Billable> search(String dr) {
		List<BillableEntity> l = repository.findByDeliveryNotNullAndOrderNoContainingIgnoreCase(dr);
		return toModels(l);
	}

	@Override
	public BillableEntity toEntity(Billable b) {
		BillableEntity e = super.toEntity(b);
		if (e == null)
			return null;
		e.setOrderNo(b.getDeliveryNo());
		e.setCustomer(customer(b.getVendor()));
		e.setDelivery(deliveryDetail(b));
		return e;
	}

	private DeliveryDetailEntity deliveryDetail(Billable b) {
		DeliveryDetailEntity d = new DeliveryDetailEntity();
		d.setRecipient(customer(b.getClient()));
		d.setPlateNo(b.getTruckPlateNo());
		d.setScaleNo(b.getTruckScaleNo());
		d.setGrossWeight(b.getGrossWeight());
		d.setFfa(b.getFfaPercent());
		d.setColor(b.getColor());
		d.setIodine(b.getIodineQty());
		return d;
	}

	@Override
	protected List<BillableEntity> deliveries(BillableEntity e) {
		return null;
	}
}