package ph.txtdis.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.BomEntity;
import ph.txtdis.domain.ItemEntity;
import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.type.BillableType;

@Service("deliveryVarianceService")
public class DeliveryVarianceServiceImpl implements DeliveryVarianceService {

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
	public List<SalesItemVariance> listByDate(LocalDate start, LocalDate end) {
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
		v.setQtyPerCase(itemService.getQtyPerCase(e));
		v.setPriceValue(priceService.getLatest("PURCHASE", e));
		return v;
	}

	private void mapDeliveryListAndLoadManifestQty(LocalDate start, LocalDate end) {
		mapDeliveryListQty(start, end);
		mapLoadManifestQty(start, end);
	}

	private void mapDeliveryListQty(LocalDate start, LocalDate end) {
		List<BomEntity> l = deliveryListService.getBomList(start, end);
		l.forEach(b -> mapQty(BillableType.DELIVERY_LIST, b));
	}

	private void mapQty(BillableType module, BomEntity bom) {
		String vendorId = bom.getPart().getVendorId();
		SalesItemVariance variance = map.get(vendorId);
		int qty = bom.getQty().intValue();
		if (module == BillableType.DELIVERY_LIST)
			variance.setActual(qty);
		else
			variance.setExpected(qty);
		map.put(vendorId, variance);
	}

	private void mapLoadManifestQty(LocalDate start, LocalDate end) {
		List<BomEntity> l = loadManifestService.getBomList(start, end);
		l.forEach(b -> mapQty(BillableType.LOAD_MANIFEST, b));
	}

	private List<SalesItemVariance> listOnlyItemsFoundOnDeliveryListOrLoadManifest() {
		return map.values().stream().filter(v -> v.getActual() > 0 || v.getExpected() > 0).collect(Collectors.toList());
	}
}