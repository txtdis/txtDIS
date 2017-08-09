package ph.txtdis.mgdc.ccbpi.service.server;

import static java.util.stream.Collectors.toList;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.PriceType.DEALER;
import static ph.txtdis.type.QuantityType.ACTUAL;
import static ph.txtdis.type.QuantityType.EXPECTED;
import static ph.txtdis.type.QuantityType.OTHER;
import static ph.txtdis.type.QuantityType.RETURNED;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;
import static ph.txtdis.util.NumberUtils.toCurrencyText;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.mgdc.ccbpi.domain.BomEntity;
import ph.txtdis.mgdc.ccbpi.domain.DetailedEntity;
import ph.txtdis.mgdc.ccbpi.domain.ItemEntity;
import ph.txtdis.type.QuantityType;

@Service("detailsToVarianceService")
public class DetailsToVarianceServiceImpl //
	implements DetailsToVarianceService {

	private static Logger logger = getLogger(DetailsToVarianceServiceImpl.class);

	@Autowired
	private DetailsToBomService bomService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private PriceService priceService;

	private FilteredListService actualService, expectedService, otherService, returnedService;

	private Map<String, SalesItemVariance> map;

	public DetailsToVarianceServiceImpl() {
		reset();
	}

	private void reset() {
		actualService = null;
		expectedService = null;
		otherService = null;
		returnedService = null;
	}

	@Override
	public void addActualService(FilteredListService service) {
		actualService = service;
	}

	@Override
	public void addExpectedService(FilteredListService service) {
		expectedService = service;
	}

	@Override
	public void addOtherService(FilteredListService service) {
		otherService = service;
	}

	@Override
	public void addReturnedService(FilteredListService service) {
		returnedService = service;
	}

	@Override
	public List<SalesItemVariance> list(String filter, LocalDate start, LocalDate end) {
		map = new HashMap<>();
		mapItemAndQty(filter, start, end);
		return listOnlyItemsWithQty();
	}

	private void mapItemAndQty(String filter, LocalDate start, LocalDate end) {
		mapItems();
		mapQty(filter, start, end);
	}

	private List<SalesItemVariance> listOnlyItemsWithQty() {
		return map.values().stream() //
			.filter(v -> v.getOtherCount() > 0 || v.getExpectedCount() > 0 || v.getActualCount() > 0 ||
				v.getReturnedCount() > 0) //
			.collect(toList());
	}

	private void mapItems() {
		List<ItemEntity> items = itemService.listEntities();
		items.stream().map(e -> toSalesItemVariance(e)).forEach(v -> map.put(v.getId().toString(), v));
	}

	private void mapQty(String filter, LocalDate start, LocalDate end) {
		mapQty(OTHER, filter, start, end);
		mapQty(EXPECTED, filter, start, end);
		mapQty(ACTUAL, filter, start, end);
		mapQty(RETURNED, filter, start, end);
	}

	private SalesItemVariance toSalesItemVariance(ItemEntity e) {
		SalesItemVariance v = new SalesItemVariance();
		v.setId(Long.valueOf(e.getVendorId()));
		v.setItem(e.getName());
		v.setQtyPerCase(itemService.getCountPerCase(e));
		return v;
	}

	private void mapQty(QuantityType type, String filter, LocalDate start, LocalDate end) {
		if (type == OTHER && otherService != null)
			mapQty(OTHER, otherService.list(filter, start, end), start);
		else if (type == EXPECTED && expectedService != null)
			mapQty(EXPECTED, expectedService.list(filter, start, end), start);
		else if (type == ACTUAL && actualService != null)
			mapQty(ACTUAL, actualService.list(filter, start, end), start);
		else if (type == RETURNED && returnedService != null)
			mapQty(RETURNED, returnedService.list(filter, start, end), start);
	}

	private void mapQty(QuantityType type, List<? extends DetailedEntity> l, LocalDate start) {
		logger.info("\n    Entities@mapQty = " + type + ": " + l);
		List<BomEntity> boms = bomService.toBomList(type, l);
		boms.forEach(b -> mapQty(type, b, start));
	}

	private void mapQty(QuantityType type, BomEntity bom, LocalDate start) {
		String vendorId = bom.getPart().getVendorId();
		int qty = bom.getQty().intValue();
		logger.info("\n    BOM@mapQty = " + type + ": " + bom.getPart() + ", " + qty);
		map.put(vendorId, setVarianceQty(type, vendorId, qty, start));
	}

	private SalesItemVariance setVarianceQty(QuantityType type, String vendorId, int qty, LocalDate start) {
		SalesItemVariance v = map.get(vendorId);
		if (type == OTHER)
			v.setOtherCount(v.getOtherCount() + qty);
		else if (type == EXPECTED)
			v.setExpectedCount(v.getExpectedCount() + qty);
		else if (type == ACTUAL)
			v.setActualCount(v.getActualCount() + qty);
		else if (type == RETURNED)
			v.setReturnedCount(v.getReturnedCount() + qty);
		v.setPriceValue(price(v, start));
		logger.info("\n    SalesItemVariance@setVarianceQty = " + type + " - " + v);
		logger.info("\n    Price@setVarianceQty = " + v.getPriceValue());
		return v;
	}

	private BigDecimal price(SalesItemVariance v, LocalDate start) {
		ItemEntity item = itemService.findEntityByName(v.getItem());
		return currentPrice(item, start).add(getEmptiesPrice(item, start));
	}

	private BigDecimal currentPrice(ItemEntity item, LocalDate start) {
		BigDecimal price = priceService.getLatestValue(DEALER.toString(), item, start);
		logger.info(
			"\n    ItemPrice@currentPrice = " + item + " @ " + toCurrencyText(price) + " on " + toDateDisplay(start));
		return price;
	}

	private BigDecimal getEmptiesPrice(ItemEntity item, LocalDate start) {
		ItemEntity empties = item.getEmpties();
		return isAnEmpty(item) || empties == null ? BigDecimal.ZERO : currentPrice(empties, start);
	}

	private boolean isAnEmpty(ItemEntity item) {
		Boolean b = item.getNotDiscounted();
		return b != null && b == true;
	}

	@SuppressWarnings("unused")
	private BigDecimal regularPrice(ItemEntity item) {
		return priceService.getRegularValue(DEALER.toString(), item);
	}
}
