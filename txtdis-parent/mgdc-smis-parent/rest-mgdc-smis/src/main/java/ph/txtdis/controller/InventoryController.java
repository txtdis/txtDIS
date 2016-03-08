package ph.txtdis.controller;

import static java.time.LocalDate.now;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static ph.txtdis.util.NumberUtils.divide;
import static ph.txtdis.util.NumberUtils.isPositive;
import static ph.txtdis.util.NumberUtils.isZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static java.math.BigDecimal.ZERO;

import ph.txtdis.domain.BillingDetail;
import ph.txtdis.domain.Stock;
import ph.txtdis.dto.Inventory;
import ph.txtdis.repository.BillingDetailRepository;
import ph.txtdis.repository.StockRepository;

@RestController("inventoryController")
@RequestMapping("/inventories")
public class InventoryController {

	private static final BigDecimal SIXTY = new BigDecimal("60");

	private static final BigDecimal NINETY = new BigDecimal("90");

	@Autowired
	private BillingDetailRepository detail;

	@Autowired
	private StockRepository stock;

	private BigDecimal price;

	@RequestMapping(method = GET)
	public ResponseEntity<?> list() {
		List<Stock> s = (List<Stock>) stock.findAll();
		List<Inventory> i = toInventory(s);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/item", method = GET)
	public ResponseEntity<?> next(@RequestParam("id") Long id) {
		Stock s = stock.findOne(id);
		Inventory i = toInventory(s);
		return new ResponseEntity<>(i, OK);
	}

	@RequestMapping(path = "/update", method = POST)
	public ResponseEntity<?> update() {
		Iterable<Stock> is = stock.save(updatedStock());
		return new ResponseEntity<>(is, OK);
	}

	private BigDecimal avgDailySoldQty(Stock s) {
		return divide(s.getSoldQty(), SIXTY);
	}

	private int daysLevel(BigDecimal soh, BigDecimal ave) {
		int i = divide(soh, ave).intValue();
		return i > 9999 ? 9999 : i;
	}

	private int daysLevel(Inventory i) {
		BigDecimal soh = i.getGoodQty();
		BigDecimal avg = i.getAvgDailySoldQty();
		if (isZero(soh))
			return isZero(avg) ? 0 : -30;
		return isZero(avg) ? 9999 : daysLevel(soh, avg);
	}

	private BigDecimal goodQty(Stock a) {
		return a.getGoodQty() == null ? ZERO : a.getGoodQty();
	}

	private BigDecimal moreThan90DayQty(Inventory i) {
		BigDecimal q = i.getGoodQty().subtract(ninetyDaySoldQty(i));
		return isPositive(q) ? q : ZERO;
	}

	private BigDecimal ninetyDaySoldQty(Inventory i) {
		BigDecimal avg = i.getAvgDailySoldQty();
		return avg.multiply(NINETY);
	}

	private BigDecimal obsolesenceValue(Inventory i) {
		return i.getDaysLevel() <= 90 ? null : moreThan90DayQty(i).multiply(price);
	}

	private LocalDate sixtyDaysAgo() {
		return now().minusDays(60L);
	}

	private List<Inventory> toInventory(List<Stock> stocks) {
		return stocks.stream().map(s -> toInventory(s)).filter(i -> i.getDaysLevel() != 0).collect(toList());
	}

	private Inventory toInventory(Stock s) {
		Inventory i = new Inventory();
		if (s != null) {
			i.setId(s.getId());
			i.setItem(s.getItem());
			i.setGoodQty(goodQty(s));
			i.setBadQty(s.getBadQty());
			i.setAvgDailySoldQty(avgDailySoldQty(s));
			i.setDaysLevel(daysLevel(i));
			i.setValue(totalValue(s));
			i.setObsolesenceValue(obsolesenceValue(i));
		}
		return i;
	}

	private Map<Long, BigDecimal> toItemSoldQtyMap(List<BillingDetail> b) {
		return b.stream().collect(//
				groupingBy(BillingDetail::getItemId, //
						mapping(BillingDetail::getUnitQty, //
								reducing(ZERO, BigDecimal::add))));
	}

	private BigDecimal totalValue(Stock a) {
		price = a.getPriceValue() == null ? ZERO : a.getPriceValue();
		return goodQty(a).multiply(price);
	}

	private List<Stock> updatedStock() {
		List<BillingDetail> b = detail.findByBillingOrderDateGreaterThanEqualOrderByItemAsc(sixtyDaysAgo());
		Map<Long, BigDecimal> m = toItemSoldQtyMap(b);
		List<Stock> s = (List<Stock>) stock.findAll();
		return updateStock(s, m);
	}

	private List<Stock> updateStock(List<Stock> stocks, Map<Long, BigDecimal> m) {
		stocks.forEach(s -> s.setSoldQty(m.get(s.getId())));
		return stocks;
	}
}