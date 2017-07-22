package ph.txtdis.mgdc.ccbpi.service.server;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.type.PriceType.PURCHASE;
import static ph.txtdis.type.QuantityType.EXPECTED;
import static ph.txtdis.type.QuantityType.RETURNED;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.mgdc.ccbpi.domain.BillableDetailEntity;
import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;
import ph.txtdis.mgdc.ccbpi.domain.BomEntity;
import ph.txtdis.mgdc.ccbpi.domain.ItemEntity;
import ph.txtdis.type.QuantityType;

@Service("paymentService")
public class DeliveryPaymentServiceImpl //
		implements DeliveryPaymentService {

	@Autowired
	private ItemService itemService;

	@Autowired
	private LoadManifestService loadManifestService;

	@Autowired
	private PriceService priceService;

	private Map<String, SalesItemVariance> map;

	@Override
	public List<SalesItemVariance> list(LocalDate d) {
		mapItemAndQty(d);
		return listOnlyDeliveredAndOrReturnedItems();
	}

	private void mapItemAndQty(LocalDate d) {
		map = new HashMap<>();
		mapAllItems();
		mapDeliveredAndReturnedQty(d);
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
		v.setPriceValue(contentAndPackagingPrice(e));
		return v;
	}

	private BigDecimal contentAndPackagingPrice(ItemEntity e) {
		return contentPrice(e).add(packagingPrice(e));
	}

	private BigDecimal contentPrice(ItemEntity e) {
		return purchasePrice(e);
	}

	private BigDecimal purchasePrice(ItemEntity e) {
		return priceService.getCurrentValue(PURCHASE.toString(), e);
	}

	private BigDecimal packagingPrice(ItemEntity e) {
		e = e.getEmpties();
		return e == null ? BigDecimal.ZERO : purchasePrice(e);
	}

	private void mapDeliveredAndReturnedQty(LocalDate d) {
		List<BillableEntity> l = loadManifestService.list(d, d);
		mapDeliveredQty(l);
		mapReturnedQty(l);
	}

	private void mapDeliveredQty(List<BillableEntity> l) {
		List<BomEntity> boms = toBomList(EXPECTED, l);
		boms.forEach(b -> mapQty(EXPECTED, b));
	}

	private void mapReturnedQty(List<BillableEntity> l) {
		List<BomEntity> boms = toBomList(RETURNED, l);
		boms.forEach(b -> mapQty(RETURNED, b));
	}

	private List<BomEntity> toBomList(QuantityType type, List<BillableEntity> billables) {
		return billables.stream().flatMap(entity -> entity.getDetails().stream()) //
				.map(d -> toBom(type, d)) //
				.collect(groupingBy(BomEntity::getPart, //
						mapping(BomEntity::getQty, reducing(ZERO, BigDecimal::add)))) //
				.entrySet().stream().map(e -> toBom(e.getKey(), e.getValue())) //
				.collect(Collectors.toList());
	}

	private BomEntity toBom(QuantityType type, BillableDetailEntity d) {
		BigDecimal qty = d.getInitialQty();
		if (type == RETURNED)
			qty = d.getReturnedQty();
		return toBom(d.getItem(), qty);
	}

	private BomEntity toBom(ItemEntity item, BigDecimal qty) {
		BomEntity b = new BomEntity();
		b.setPart(item);
		b.setQty(qty);
		return b;
	}

	private void mapQty(QuantityType type, BomEntity bom) {
		String vendorNo = bom.getPart().getVendorId();
		int qty = bom.getQty().intValue();
		SalesItemVariance v = setVarianceQty(type, vendorNo, qty);
		map.put(vendorNo, v);
	}

	private SalesItemVariance setVarianceQty(QuantityType type, String vendorId, int qty) {
		SalesItemVariance v = map.get(vendorId);
		if (type == EXPECTED)
			v.setExpectedCount(qty);
		else
			v.setReturnedCount(qty);
		return v;
	}

	private List<SalesItemVariance> listOnlyDeliveredAndOrReturnedItems() {
		return map.values().stream().filter(v -> v.getExpectedCount() > 0 || v.getReturnedCount() > 0).collect(toList());
	}
}