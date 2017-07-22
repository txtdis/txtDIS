package ph.txtdis.mgdc.ccbpi.service.server;

import static org.apache.commons.lang3.StringUtils.substringBefore;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static ph.txtdis.type.PriceType.DEALER;
import static ph.txtdis.type.QuantityType.DELIVERED;
import static ph.txtdis.type.QuantityType.RETURNED;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.mgdc.ccbpi.domain.BomEntity;
import ph.txtdis.mgdc.ccbpi.domain.ItemEntity;
import ph.txtdis.type.QuantityType;

@Service("blanketBalanceService")
public class BlanketBalanceServiceImpl //
		implements BlanketBalanceService {

	@Autowired
	private ItemService itemService;

	@Autowired
	private OrderConfirmationService orderConfirmationService;

	@Autowired
	private OrderReturnService orderReturnService;

	@Autowired
	private PriceService priceService;

	private Map<String, SalesItemVariance> map;

	@Override
	public List<SalesItemVariance> list() {
		map = new HashMap<>();
		mapDeliveredQty();
		mapReturnedQty();
		return new ArrayList<>(map.values());
	}

	private void mapQty(QuantityType module, BomEntity bom) {
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
		v.setOrderNo(orderNo(bom));
		v.setCustomer(customer(bom));
		v.setId(vendorId(bom));
		v.setItem(item(bom).getName());
		v.setQtyPerCase(itemService.getCountPerCase(item(bom)));
		v.setPriceValue(priceService.getCurrentValue(DEALER.toString(), item(bom)));
		return v;
	}

	private String routeName(BomEntity bom) {
		return StringUtils.substringAfterLast(getName(bom), "|");
	}

	public String getName(BomEntity bom) {
		return bom.getItem().getName();
	}

	private String orderNo(BomEntity bom) {
		return substringBefore(getName(bom), "|");
	}

	private String customer(BomEntity bom) {
		return substringBetween(getName(bom), "|");
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

	private SalesItemVariance setQty(SalesItemVariance variance, QuantityType type, int qty) {
		if (type == DELIVERED)
			variance.setActualCount(qty);
		else if (type == RETURNED)
			variance.setReturnedCount(qty);
		else
			variance.setExpectedCount(qty);
		return variance;
	}

	private int qty(BomEntity bom) {
		return bom.getQty().intValue();
	}

	private void mapDeliveredQty() {
		List<BomEntity> l = orderConfirmationService.getDeliveredOrderNoAndCustomerAndRouteGroupedBomList();
		l.forEach(b -> mapQty(QuantityType.DELIVERED, b));
	}

	private void mapReturnedQty() {
		List<BomEntity> l = orderReturnService.getOrderNoAndCustomerAndRouteGroupedBomList();
		l.forEach(b -> mapQty(QuantityType.RETURNED, b));
	}
}