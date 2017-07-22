package ph.txtdis.mgdc.ccbpi.service.server;

import static ph.txtdis.type.ModuleType.DELIVERY_LIST;
import static ph.txtdis.type.ModuleType.LOAD_MANIFEST;
import static ph.txtdis.type.PriceType.PURCHASE;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.mgdc.ccbpi.domain.BomEntity;
import ph.txtdis.mgdc.ccbpi.domain.ItemEntity;
import ph.txtdis.type.ModuleType;

@Service("deliveryVarianceService")
public class DeliveryVarianceServiceImpl //
		implements DeliveryVarianceService {

	@Autowired
	private DeliveryListService deliveryListService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private LoadManifestService loadManifestService;

	@Autowired
	private PriceService priceService;

	private Map<String, SalesItemVariance> map;

	@Override
	public List<SalesItemVariance> list(LocalDate start, LocalDate end) {
		mapItemAndQty(start, end);
		return listOnlyItemsFoundOnDeliveryListOrLoadManifest();
	}

	private void mapItemAndQty(LocalDate start, LocalDate end) {
		map = new HashMap<>();
		mapAllItems();
		mapDeliveryListAndLoadManifestQty(start, end);
	}

	private void mapAllItems() {
		List<ItemEntity> items = itemService.listEntities();
		items.stream().map(e -> toSalesItemVariance(e)).forEach(v -> map.put(v.getId().toString(), v));
	}

	private SalesItemVariance toSalesItemVariance(ItemEntity e) {
		SalesItemVariance v = new SalesItemVariance();
		v.setId(Long.valueOf(e.getVendorId()));
		v.setItem(e.getName());
		v.setQtyPerCase(itemService.getCountPerCase(e));
		v.setPriceValue(priceService.getCurrentValue(PURCHASE.toString(), e));
		return v;
	}

	private void mapDeliveryListAndLoadManifestQty(LocalDate start, LocalDate end) {
		mapDeliveryListQty(start, end);
		mapLoadManifestQty(start, end);
	}

	private void mapDeliveryListQty(LocalDate start, LocalDate end) {
		List<BomEntity> l = deliveryListService.getBomList("ALL", start, end);
		l.forEach(b -> mapQty(DELIVERY_LIST, b));
	}

	private void mapQty(ModuleType module, BomEntity bom) {
		String vendorId = bom.getPart().getVendorId();
		SalesItemVariance variance = map.get(vendorId);
		int qty = bom.getQty().intValue();
		if (module == DELIVERY_LIST)
			variance.setActualCount(qty);
		else
			variance.setExpectedCount(qty);
		map.put(vendorId, variance);
	}

	private void mapLoadManifestQty(LocalDate start, LocalDate end) {
		List<BomEntity> l = loadManifestService.getBomList(start, end);
		l.forEach(b -> mapQty(LOAD_MANIFEST, b));
	}

	private List<SalesItemVariance> listOnlyItemsFoundOnDeliveryListOrLoadManifest() {
		return map.values().stream().filter(v -> v.getActualCount() > 0 || v.getExpectedCount() > 0).collect(Collectors.toList());
	}
}