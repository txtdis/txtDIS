package ph.txtdis.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.BomEntity;
import ph.txtdis.domain.ItemEntity;
import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.type.BillableType;

@Service("bookingVarianceService")
public class BookingVarianceServiceImpl implements BookingVarianceService {

	@Autowired
	private DeliveryListService deliveryListService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private OrderConfirmationService orderConfirmationService;

	@Autowired
	private PriceService priceService;

	private Map<String, SalesItemVariance> map;

	@Override
	public List<SalesItemVariance> listByDate(LocalDate start, LocalDate end) {
		map = new HashMap<>();
		mapDeliveryListQty(start, end);
		mapOrderConfirmationQty(start, end);
		return new ArrayList<>(map.values());
	}

	private void mapDeliveryListQty(LocalDate start, LocalDate end) {
		List<BomEntity> l = deliveryListService.getRouteGroupedBomList(start, end);
		l.forEach(b -> mapQty(BillableType.DELIVERY_LIST, b));
	}

	private void mapQty(BillableType module, BomEntity bom) {
		SalesItemVariance variance = getVariance(bom);
		variance = setQty(variance, module, qty(bom));
		map.put(key(bom), variance);
	}

	private SalesItemVariance getVariance(BomEntity bom) {
		SalesItemVariance v = map.get(key(bom));
		return v != null ? v : mapNewVariance(bom);
	}

	private SalesItemVariance mapNewVariance(BomEntity bom) {
		SalesItemVariance v = newSalesItemVariance(bom);
		map.put(key(bom), v);
		return v;
	}

	private SalesItemVariance newSalesItemVariance(BomEntity bom) {
		SalesItemVariance v = new SalesItemVariance();
		v.setSeller(routeName(bom));
		v.setId(vendorId(bom));
		v.setItem(item(bom).getName());
		v.setQtyPerCase(itemService.getQtyPerCase(item(bom)));
		v.setPriceValue(priceService.getLatest("DEALER", item(bom)));
		return v;
	}

	private String routeName(BomEntity bom) {
		return bom.getItem().getName();
	}

	private Long vendorId(BomEntity bom) {
		String vendorId = item(bom).getVendorId();
		return Long.valueOf(vendorId);
	}

	private ItemEntity item(BomEntity bom) {
		return bom.getPart();
	}

	private String key(BomEntity bom) {
		return vendorId(bom) + routeName(bom);
	}

	private SalesItemVariance setQty(SalesItemVariance variance, BillableType module, int qty) {
		if (module == BillableType.DELIVERY_LIST)
			variance.setActual(qty);
		else
			variance.setExpected(qty);
		return variance;
	}

	private int qty(BomEntity bom) {
		return bom.getQty().intValue();
	}

	private void mapOrderConfirmationQty(LocalDate start, LocalDate end) {
		List<BomEntity> l = orderConfirmationService.getRouteGroupedBomList(start, end);
		l.forEach(b -> mapQty(BillableType.ORDER_CONFIRMATION, b));
	}
}