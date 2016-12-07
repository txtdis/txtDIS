package ph.txtdis.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.domain.BomEntity;
import ph.txtdis.domain.ItemEntity;
import ph.txtdis.domain.PriceEntity;
import ph.txtdis.domain.QtyPerUomEntity;
import ph.txtdis.domain.VolumeDiscountEntity;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Bom;
import ph.txtdis.dto.Item;
import ph.txtdis.repository.ItemRepository;
import ph.txtdis.type.UomType;

public abstract class AbstractItemService extends AbstractSpunService<ItemRepository, ItemEntity, Item, Long>
		implements ItemService {

	@Autowired
	private BomService bomService;

	@Autowired
	private PrimaryItemFamilyService familyService;

	@Autowired
	private PriceService pricingService;

	@Autowired
	private QtyPerUomService qtyPerUomService;

	@Autowired
	private VolumeDiscountService discountService;

	@Override
	public void delete(Long id) {
		repository.delete(id);
	}

	@Override
	public Item findByName(String name) {
		ItemEntity e = repository.findByName(name);
		return e == null ? null : toDTO(e);
	}

	@Override
	public Item findByVendorId(String id) {
		ItemEntity e = repository.findByVendorId(id);
		return e == null ? null : toDTO(e);
	}

	@Override
	public List<Item> list() {
		return toIdAndNameOnlyList(listEntities());
	}

	@Override
	public List<ItemEntity> listEntities() {
		return repository.findByOrderById();
	}

	@Override
	public List<Item> searchByDescription(String description) {
		List<ItemEntity> l = repository.findByDescriptionContaining(description);
		return toIdAndNameOnlyList(l);
	}

	protected List<Item> toIdAndNameOnlyList(List<ItemEntity> l) {
		return l == null ? null : l.stream().map(i -> idAndNameOnly(i)).collect(Collectors.toList());
	}

	private Item idAndNameOnly(ItemEntity e) {
		if (e == null)
			return null;
		Item i = new Item();
		i.setId(e.getId());
		i.setName(e.getName());
		i.setDescription(e.getDescription());
		i.setVendorId(e.getVendorId());
		return i;
	}

	@Override
	public ItemEntity toEntity(Item i) {
		ItemEntity e = findSavedEntity(i);
		if (e == null)
			e = newEntity(i);
		if (e.getDeactivatedBy() == null && i.getDeactivatedBy() != null)
			return deactivate(e, i);
		if (i.getPriceList() != null)
			e.setPriceList(getPriceList(e, i));
		if (i.getVolumeDiscounts() != null)
			e.setVolumeDiscounts(getVolumeDiscounts(e, i));
		return e;
	}

	private ItemEntity findSavedEntity(Item t) {
		if (t == null)
			return null;
		if (t.getId() != null)
			return repository.findOne(t.getId());
		return repository.findByName(t.getName());
	}

	private ItemEntity newEntity(Item i) {
		ItemEntity e = new ItemEntity();
		e.setName(i.getName());
		e.setDescription(i.getDescription());
		e.setEmpties(findEntityByDescription(i));
		e.setBoms(getBoms(i));
		e.setVendorId(i.getVendorId());
		e.setNotDiscounted(i.isNotDiscounted());
		e.setQtyPerUomList(getQtyPerUomList(e, i));
		e.setType(i.getType());
		e.setFamily(familyService.toEntity(i.getFamily()));
		e.setEndOfLife(i.getEndOfLife());
		return e;
	}

	private ItemEntity findEntityByDescription(Item i) {
		return repository.findByDescription(i.getEmpties());
	}

	private List<QtyPerUomEntity> getQtyPerUomList(ItemEntity e, Item i) {
		return qtyPerUomService.toEntities(e, i);
	}

	private List<BomEntity> getBoms(Item i) {
		List<Bom> boms = i.getBoms();
		return bomService.toEntities(boms);
	}

	private ItemEntity deactivate(ItemEntity i, Item t) {
		i.setDeactivatedBy(t.getDeactivatedBy());
		i.setDeactivatedOn(ZonedDateTime.now());
		return i;
	}

	private List<PriceEntity> getPriceList(ItemEntity e, Item i) {
		if (e.getPriceList() == null)
			return pricingService.toEntities(i.getPriceList());
		if (e.getPriceList().size() != i.getPriceList().size())
			return pricingService.newPrices(e, i);
		if (pricingService.decisionOnNewPricingMade(e, i))
			return pricingService.getUpdatedPricingDecisions(e, i);
		return e.getPriceList();
	}

	private List<VolumeDiscountEntity> getVolumeDiscounts(ItemEntity e, Item i) {
		if (e.getVolumeDiscounts() == null)
			return discountService.toEntities(i.getVolumeDiscounts());
		if (e.getVolumeDiscounts().size() != i.getVolumeDiscounts().size())
			return discountService.newDiscounts(e, i);
		if (discountService.decisionOnNewVolumeDiscountMade(e, i))
			return discountService.getUpdatedVolumeDiscountDecisions(e, i);
		return e.getVolumeDiscounts();
	}

	@Override
	public Item toDTO(ItemEntity e) {
		Item i = idAndNameOnly(e);
		if (i == null)
			return null;
		i.setEmpties(getEmpties(e));
		i.setBoms(bomService.toList(e.getBoms()));
		i.setEndOfLife(e.getEndOfLife());
		i.setFamily(familyService.toDTO(e.getFamily()));
		i.setNotDiscounted(e.isNotDiscounted());
		i.setPriceList(pricingService.toList(e.getPriceList()));
		i.setQtyPerUomList(qtyPerUomService.toList(e.getQtyPerUomList()));
		i.setType(e.getType());
		i.setVendorId(e.getVendorId());
		i.setVolumeDiscounts(discountService.toList(e.getVolumeDiscounts()));
		i.setCreatedBy(e.getCreatedBy());
		i.setCreatedOn(e.getCreatedOn());
		i.setDeactivatedBy(e.getDeactivatedBy());
		i.setDeactivatedOn(e.getDeactivatedOn());
		i.setLastModifiedBy(e.getLastModifiedBy());
		i.setLastModifiedOn(e.getLastModifiedOn());
		return i;
	}

	private String getEmpties(ItemEntity e) {
		ItemEntity empties = e.getEmpties();
		return empties == null ? null : empties.getDescription();
	}

	@Override
	public ItemEntity findEntity(BillableDetail bd) {
		Long id = bd.getId();
		if (id == null)
			return repository.findByVendorId(bd.getItemVendorNo());
		return repository.findOne(id);
	}

	@Override
	public int getQtyPerCase(ItemEntity item) {
		try {
			return item.getQtyPerUomList().stream()//
					.filter(u -> u.getUom() == UomType.CS)//
					.findAny().get().getQty().intValue();
		} catch (Exception e) {
			return 0;
		}
	}
}