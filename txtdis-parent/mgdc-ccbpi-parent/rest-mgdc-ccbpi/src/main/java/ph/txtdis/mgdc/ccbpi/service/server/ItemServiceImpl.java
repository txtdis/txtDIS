package ph.txtdis.mgdc.ccbpi.service.server;

import static java.util.stream.Collectors.toList;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.BeverageType.EMPTIES;
import static ph.txtdis.type.UomType.CS;
import static ph.txtdis.util.NumberUtils.divide;
import static ph.txtdis.util.TextUtils.nullIfEmpty;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.mgdc.ccbpi.domain.BomEntity;
import ph.txtdis.mgdc.ccbpi.domain.ItemEntity;
import ph.txtdis.mgdc.ccbpi.domain.QtyPerUomEntity;
import ph.txtdis.mgdc.ccbpi.dto.Item;
import ph.txtdis.mgdc.ccbpi.repository.ItemRepository;
import ph.txtdis.mgdc.domain.PriceEntity;
import ph.txtdis.mgdc.service.server.ConvertibleItemFamilyService;
import ph.txtdis.service.AbstractSpunSavedKeyedService;
import ph.txtdis.type.PriceType;

@Service("itemService")
public class ItemServiceImpl
	extends AbstractSpunSavedKeyedService<ItemRepository, ItemEntity, Item, Long> //
	implements ServerEmptiesItemService {

	private static Logger logger = getLogger(ItemServiceImpl.class);

	@Autowired
	private BomService bomService;

	@Autowired
	private ConvertibleItemFamilyService familyService;

	@Autowired
	private PriceService priceService;

	@Autowired
	private QtyPerUomService qtyPerUomService;

	private Map<ItemEntity, BigDecimal> map;

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
	public ItemEntity findEntityByName(String name) {
		return repository.findByName(name);
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
		i.setEmpties(getEmpties(e));
		i.setBoms(bomService.toBoms(e.getBoms()));
		i.setFamily(familyService.toModel(e.getFamily()));
		i.setPriceList(priceService.toModels(e.getPriceList()));
		return i;
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

	private boolean isNotDiscounted(ItemEntity e) {
		return e.getNotDiscounted() != null && e.getNotDiscounted() == true;
	}

	private String getEmpties(ItemEntity e) {
		ItemEntity empties = e.getEmpties();
		return empties == null ? null : empties.getName();
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
	public int getCountPerCase(ItemEntity item) {
		return getQtyPerCase(item).intValue();
	}

	@Override
	public BigDecimal getQtyPerCase(ItemEntity item) {
		return qtyPerUomService.getItemQtyPerUom(item, CS);
	}

	@Override
	public List<Item> list() {
		return toIdAndNameOnlyList(listEntities());
	}

	private List<Item> toIdAndNameOnlyList(List<ItemEntity> l) {
		return l == null ? null : l.stream().map(i -> idAndNameOnly(i)).collect(Collectors.toList());
	}

	@Override
	public List<ItemEntity> listEntities() {
		return repository.findByOrderById();
	}

	@Override
	public List<Item> listEmpties() {
		List<ItemEntity> l = listEmptiesEntities();
		return toIdAndNameOnlyList(l);
	}

	private List<ItemEntity> listEmptiesEntities() {
		return repository.findByFamilyName(EMPTIES.toString());
	}

	@Override
	public List<Item> listFully() {
		List<ItemEntity> l = repository.findByOrderById();
		return l == null ? null : l.stream().map(e -> toModel(e)).collect(toList());
	}

	@Override
	public Map<ItemEntity, BigDecimal> mapEmptiesPriceValue(PriceType type, List<BomEntity> boms) {
		map = new HashMap<>();
		listEmptiesEntities().forEach(i -> mapEmpties(type, i));
		boms.forEach(b -> mapPriceValue(b));
		return map;
	}

	private BigDecimal mapEmpties(PriceType type, ItemEntity item) {
		return map.put(filterPrice(type, item), BigDecimal.ZERO);
	}

	private void mapPriceValue(BomEntity b) {
		ItemEntity item = b.getPart();
		logger.info("\n    Item@mapPriceValue = " + item);
		logger.info("\n    Qty@mapPriceValue = " + b.getQty());
		logger.info("\n    Price@mapPriceValue = " + unitPrice(item));
		map.replace(item, addPriceValues(item, b.getQty()));
	}

	private ItemEntity filterPrice(PriceType type, ItemEntity item) {
		item.setPriceList(Arrays.asList(priceService.getRegularEntity(type.toString(), item)));
		return item;
	}

	private BigDecimal unitPrice(ItemEntity item) {
		return divide(priceValue(item), getQtyPerCase(item));
	}

	private BigDecimal addPriceValues(ItemEntity item, BigDecimal qty) {
		return map.get(item).add(priceValue(item, qty));
	}

	private BigDecimal priceValue(ItemEntity item) {
		return item.getPriceList().get(0).getPriceValue();
	}

	private BigDecimal priceValue(ItemEntity item, BigDecimal qty) {
		return unitPrice(item).multiply(qty);
	}

	@Override
	public List<Item> searchByDescription(String description) {
		List<ItemEntity> l = repository.findByDescriptionContaining(description);
		return toIdAndNameOnlyList(l);
	}

	@Override
	public ItemEntity toEntity(Item i) {
		ItemEntity e = findSavedEntity(i);
		if (e == null)
			e = newEntity(i);
		if (e.getDeactivatedBy() == null && i.getDeactivatedBy() != null)
			return deactivate(e, i);
		e.setVendorId(nullIfEmpty(i.getVendorNo()));
		if (i.getPriceList() != null)
			e.setPriceList(priceList(e, i));
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
		e.setVendorId(i.getVendorNo());
		e.setNotDiscounted(i.isNotDiscounted() != true ? null : true);
		e.setQtyPerUomList(getQtyPerUomList(e, i));
		e.setType(i.getType());
		e.setEndOfLife(i.getEndOfLife());
		e.setFamily(familyService.toEntity(i.getFamily()));
		e.setBoms(getBoms(e, i));
		return e;
	}

	private ItemEntity deactivate(ItemEntity i, Item t) {
		i.setDeactivatedBy(t.getDeactivatedBy());
		i.setDeactivatedOn(ZonedDateTime.now());
		return i;
	}

	private List<PriceEntity> priceList(ItemEntity e, Item i) {
		if (e.getPriceList() == null)
			return priceService.toEntities(i.getPriceList());
		if (e.getPriceList().size() < i.getPriceList().size())
			return priceService.getNewAndOldPriceEntities(e, i);
		if (priceService.hasDecisionOnNewPricingBeenMade(e, i))
			return priceService.updatePricingDecisions(e, i);
		return e.getPriceList();
	}

	private ItemEntity findEntityByDescription(Item i) {
		return repository.findByName(i.getEmpties());
	}

	private List<QtyPerUomEntity> getQtyPerUomList(ItemEntity e, Item i) {
		return qtyPerUomService.toEntities(e, i);
	}

	private List<BomEntity> getBoms(ItemEntity e, Item i) {
		return bomService.toEntities(e, i);
	}
}