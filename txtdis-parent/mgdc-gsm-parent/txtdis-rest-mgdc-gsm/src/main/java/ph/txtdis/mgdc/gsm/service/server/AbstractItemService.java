package ph.txtdis.mgdc.gsm.service.server;

import static java.util.stream.Collectors.toList;
import static ph.txtdis.type.UomType.CS;
import static ph.txtdis.util.TextUtils.nullIfEmpty;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.gsm.domain.ItemEntity;
import ph.txtdis.mgdc.gsm.domain.QtyPerUomEntity;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.mgdc.gsm.repository.ItemRepository;
import ph.txtdis.service.AbstractSpunSavedKeyedService;

public abstract class AbstractItemService //
		extends AbstractSpunSavedKeyedService<ItemRepository, ItemEntity, Item, Long> //
		implements BillableDetailedItemService {

	@Autowired
	private QtyPerUomService qtyPerUomService;

	@Override
	public void delete(Long id) {
		repository.delete(id);
	}

	@Override
	public Item findByName(String name) {
		ItemEntity e = findEntityByName(name);
		return e == null ? null : toModel(e);
	}

	@Override
	public Item findByVendorId(String id) {
		ItemEntity e = repository.findByVendorId(id);
		return e == null ? null : toModel(e);
	}

	@Override
	public ItemEntity findEntity(BillableDetail d) {
		if (d == null)
			return null;
		Long id = d.getId();
		if (id == null)
			return repository.findByVendorId(d.getItemVendorNo());
		return repository.findOne(id);
	}

	@Override
	public ItemEntity findEntityByName(String name) {
		return repository.findByName(name);
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
	public List<Item> listFully() {
		List<ItemEntity> l = repository.findByOrderById();
		return l == null ? null : l.stream().map(e -> toModel(e)).collect(toList());
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
		i.setVendorNo(e.getVendorId());
		return i;
	}

	@Override
	public ItemEntity toEntity(Item i) {
		ItemEntity e = findSavedEntity(i);
		if (e == null)
			e = newEntity(i);
		if (e.getDeactivatedBy() == null && i.getDeactivatedBy() != null)
			return deactivate(e, i);
		e.setVendorId(nullIfEmpty(i.getVendorNo()));
		return e;
	}

	private ItemEntity findSavedEntity(Item t) {
		if (t == null)
			return null;
		if (t.getId() != null)
			return repository.findOne(t.getId());
		return repository.findByName(t.getName());
	}

	protected ItemEntity newEntity(Item i) {
		ItemEntity e = new ItemEntity();
		e.setName(i.getName());
		e.setDescription(i.getDescription());
		e.setVendorId(i.getVendorNo());
		e.setNotDiscounted(i.isNotDiscounted() != true ? null : true);
		e.setQtyPerUomList(getQtyPerUomList(e, i));
		e.setType(i.getType());
		e.setEndOfLife(i.getEndOfLife());
		return e;
	}

	private List<QtyPerUomEntity> getQtyPerUomList(ItemEntity e, Item i) {
		return qtyPerUomService.toEntities(e, i);
	}

	private ItemEntity deactivate(ItemEntity i, Item t) {
		i.setDeactivatedBy(t.getDeactivatedBy());
		i.setDeactivatedOn(ZonedDateTime.now());
		return i;
	}

	@Override
	public Item toModel(ItemEntity e) {
		Item i = idAndNameOnly(e);
		if (i == null)
			return null;
		i.setEndOfLife(e.getEndOfLife());
		i.setNotDiscounted(isNotDiscounted(e));
		i.setQtyPerUomList(qtyPerUomService.toList(e.getQtyPerUomList()));
		i.setType(e.getType());
		i.setVendorNo(e.getVendorId());
		i.setCreatedBy(e.getCreatedBy());
		i.setCreatedOn(e.getCreatedOn());
		i.setDeactivatedBy(e.getDeactivatedBy());
		i.setDeactivatedOn(e.getDeactivatedOn());
		i.setLastModifiedBy(e.getLastModifiedBy());
		i.setLastModifiedOn(e.getLastModifiedOn());
		return i;
	}

	private boolean isNotDiscounted(ItemEntity e) {
		return e.getNotDiscounted() != null && e.getNotDiscounted() == true;
	}

	@Override
	public int getCountPerCase(ItemEntity item) {
		return getQtyPerCase(item).intValue();
	}

	@Override
	public BigDecimal getQtyPerCase(ItemEntity item) {
		return qtyPerUomService.getItemQtyPerUom(item, CS);
	}
}