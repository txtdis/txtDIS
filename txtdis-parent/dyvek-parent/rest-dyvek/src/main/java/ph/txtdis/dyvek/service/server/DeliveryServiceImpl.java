package ph.txtdis.dyvek.service.server;

import static java.util.Collections.emptyList;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import ph.txtdis.dyvek.domain.BillableEntity;
import ph.txtdis.dyvek.domain.DeliveryDetailEntity;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.repository.DeliveryRepository;

@Service("deliveryService")
public class DeliveryServiceImpl //
		extends AbstractSpunSavedBillableService<DeliveryRepository> //
		implements DeliveryService {

	@Override
	public List<BillableEntity> findAllByVendorPurchaseNo(Long vendorId, String po) {
		return po == null ? emptyList() : repository.findByDeliveryNotNullAndReferencesReferenceCustomerIdAndReferencesReferenceOrderNo(vendorId, po);
	}

	@Override
	public Billable findByVendorDeliveryNo(String vendor, String dr) {
		BillableEntity e = repository.findByDeliveryNotNullAndCustomerNameAndOrderNo(vendor, dr);
		return toModel(e);
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
		d.setColor(b.getColor());
		d.setIodine(b.getIodineQty());
		d.setLauric(b.getLauricPercent());
		d.setOleic(b.getOleicPercent());
		d.setMoisture(b.getMoisturePercent());
		d.setSaponification(b.getSaponificationPercent());
		return d;
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
		b.setColor(color(e));
		b.setIodineQty(iodineValue(e));
		b.setLauricPercent(lauricFFA(e));
		b.setOleicPercent(oleicFFA(e));
		b.setMoisturePercent(moistureContent(e));
		b.setSaponificationPercent(saponificationIndex(e));
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

	private String color(BillableEntity e) {
		DeliveryDetailEntity d = e.getDelivery();
		return d == null ? null : d.getColor();
	}

	private BigDecimal iodineValue(BillableEntity e) {
		DeliveryDetailEntity d = e.getDelivery();
		return d == null ? null : d.getIodine();
	}

	private BigDecimal lauricFFA(BillableEntity e) {
		DeliveryDetailEntity d = e.getDelivery();
		return d == null ? null : d.getLauric();
	}

	private BigDecimal oleicFFA(BillableEntity e) {
		DeliveryDetailEntity d = e.getDelivery();
		return d == null ? null : d.getOleic();
	}

	private BigDecimal moistureContent(BillableEntity e) {
		DeliveryDetailEntity d = e.getDelivery();
		return d == null ? null : d.getMoisture();
	}

	private BigDecimal saponificationIndex(BillableEntity e) {
		DeliveryDetailEntity d = e.getDelivery();
		return d == null ? null : d.getSaponification();
	}

	@Override
	protected List<BillableEntity> deliveries(BillableEntity e) {
		return null;
	}
}