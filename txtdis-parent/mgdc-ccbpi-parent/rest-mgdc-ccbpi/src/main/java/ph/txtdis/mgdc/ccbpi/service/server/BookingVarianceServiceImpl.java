package ph.txtdis.mgdc.ccbpi.service.server;

import static ph.txtdis.type.PriceType.DEALER;
import static ph.txtdis.type.PriceType.PURCHASE;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
import ph.txtdis.mgdc.ccbpi.domain.CustomerEntity;
import ph.txtdis.mgdc.ccbpi.domain.ItemEntity;
import ph.txtdis.type.ModuleType;
import ph.txtdis.util.DateTimeUtils;

@Service("bookingVarianceService")
public class BookingVarianceServiceImpl //
		implements BookingVarianceService {

	@Autowired
	private DeliveryListService deliveryListService;

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
	public List<SalesItemVariance> list(String route, LocalDate start, LocalDate end) {
		map = new HashMap<>();
		mapOrderConfirmationQty(route, start, end);
		mapDeliveryListQty(route, start, end);
		mapOrderReturnQty(route, start, end);
		return new ArrayList<>(map.values());
	}

	private void mapOrderConfirmationQty(String route, LocalDate start, LocalDate end) {
		List<BomEntity> l = orderConfirmationService.getBomList(route, start, end);
		l.forEach(b -> mapQty(ModuleType.ORDER_CONFIRMATION, b));
	}

	private void mapQty(ModuleType module, BomEntity bom) {
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
		v.setId(vendorId(bom));
		v.setItem(item(bom).getName());
		v.setQtyPerCase(itemService.getCountPerCase(item(bom)));
		v.setPriceValue(priceService.getCurrentValue(DEALER.toString(), item(bom)));
		return v;
	}

	private Long vendorId(BomEntity bom) {
		String vendorId = item(bom).getVendorId();
		return Long.valueOf(vendorId);
	}

	private ItemEntity item(BomEntity bom) {
		return bom.getPart();
	}

	private String key(BomEntity bom) {
		return "" + vendorId(bom);
	}

	private SalesItemVariance setQty(SalesItemVariance variance, ModuleType module, int qty) {
		if (module == ModuleType.DELIVERY_LIST)
			variance.setActualCount(qty);
		else if (module == ModuleType.SALES_RETURN)
			variance.setReturnedCount(qty);
		else
			variance.setExpectedCount(qty);
		return variance;
	}

	private int qty(BomEntity bom) {
		return bom.getQty().intValue();
	}

	private void mapDeliveryListQty(String route, LocalDate start, LocalDate end) {
		List<BomEntity> l = deliveryListService.getBomList(route, start, end);
		l.forEach(b -> mapQty(ModuleType.DELIVERY_LIST, b));
	}

	private void mapOrderReturnQty(String route, LocalDate start, LocalDate end) {
		List<BomEntity> l = orderReturnService.getBomList(start, end);
		l.forEach(b -> mapQty(ModuleType.SALES_RETURN, b));
	}

	@Override
	public List<SalesItemVariance> listDDL(String itemVendorNo, String route, LocalDate start, LocalDate end) {
		List<BillableDetailEntity> l = deliveryListService.getDetailEntityList(itemVendorNo, route, start, end);
		return l.stream().map(d -> newSalesItemVariance(d)).collect(Collectors.toList());
	}

	private SalesItemVariance newSalesItemVariance(BillableDetailEntity d) {
		SalesItemVariance v = new SalesItemVariance();
		v.setId(ocs(d).getId());
		v.setSeller(route(d));
		v.setOrderNo(orderNo(d));
		v.setCustomer(customerName(d));
		v.setQtyPerCase(qtyPerCase(d));
		v.setPriceValue(price(d));
		v.setExpectedCount(bookedQty(d));
		v.setReturnedCount(returnedQty(d));
		return v;
	}

	private BillableEntity ocs(BillableDetailEntity d) {
		return d.getBilling();
	}

	private String route(BillableDetailEntity d) {
		return ocs(d).getSuffix();
	}

	private String orderNo(BillableDetailEntity d) {
		return isDDL(d) ? shipmentNo(d) : ocsNo(d);
	}

	private boolean isDDL(BillableDetailEntity d) {
		return customer(d) == null;
	}

	private CustomerEntity customer(BillableDetailEntity d) {
		return ocs(d).getCustomer();
	}

	private String shipmentNo(BillableDetailEntity d) {
		return ocs(d).getBookingId().toString();
	}

	private String ocsNo(BillableDetailEntity d) {
		return customerVendorNo(d) + "-" + ocsDate(d) + "/" + ocsOrderNo(d);
	}

	private Long customerVendorNo(BillableDetailEntity d) {
		return customer(d).getVendorId();
	}

	private String ocsDate(BillableDetailEntity d) {
		return DateTimeUtils.toOrderConfirmationDate(ocs(d).getOrderDate());
	}

	private Long ocsOrderNo(BillableDetailEntity d) {
		return ocs(d).getBookingId();
	}

	private String customerName(BillableDetailEntity d) {
		return customer(d) == null ? null : customer(d).getName();
	}

	private int qtyPerCase(BillableDetailEntity d) {
		return itemService.getCountPerCase(d.getItem());
	}

	private BigDecimal price(BillableDetailEntity d) {
		return isDDL(d) ? purchasePrice(d) : dealerPrice(d);
	}

	private BigDecimal purchasePrice(BillableDetailEntity d) {
		return price(PURCHASE.toString(), d.getItem(), ocs(d).getOrderDate());
	}

	private BigDecimal price(String pricingType, ItemEntity item, LocalDate date) {
		return priceService.getLatestValue(pricingType, item, date);
	}

	private BigDecimal dealerPrice(BillableDetailEntity d) {
		return price(DEALER.toString(), d.getItem(), ocs(d).getDueDate());
	}

	private int bookedQty(BillableDetailEntity d) {
		return d.getInitialQty().intValue();
	}

	private int returnedQty(BillableDetailEntity d) {
		return d.getReturnedQty().intValue();
	}

	@Override
	public List<SalesItemVariance> listOCS(String itemVendorNo, String route, LocalDate start, LocalDate end) {
		List<BillableDetailEntity> l = orderConfirmationService.getDetailEntityList(itemVendorNo, route, start, end);
		return l.stream().map(d -> newSalesItemVariance(d)).collect(Collectors.toList());
	}

	@Override
	public List<SalesItemVariance> listRR(String itemVendorNo, String route, LocalDate start, LocalDate end) {
		List<BillableDetailEntity> l = orderReturnService.getDetailEntityList(itemVendorNo, route, start, end);
		return l.stream().map(d -> newSalesItemVariance(d)).collect(Collectors.toList());
	}
}