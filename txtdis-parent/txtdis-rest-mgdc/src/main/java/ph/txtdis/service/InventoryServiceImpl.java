package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static java.time.LocalDate.now;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.NumberUtils.divide;
import static ph.txtdis.util.NumberUtils.isPositive;
import static ph.txtdis.util.NumberUtils.isZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import ph.txtdis.domain.BillableDetailEntity;
import ph.txtdis.domain.StockEntity;
import ph.txtdis.dto.Inventory;
import ph.txtdis.dto.Stock;
import ph.txtdis.repository.BillingDetailRepository;
import ph.txtdis.repository.StockRepository;

@Service("inventoryService")
public class InventoryServiceImpl implements InventoryService {

	private static final BigDecimal SIXTY = new BigDecimal("60");

	private static final BigDecimal NINETY = new BigDecimal("90");

	@Autowired
	private BillingDetailRepository detail;

	@Autowired
	private StockRepository stock;

	private BigDecimal price;

	@Override
	public List<Inventory> list() {
		List<StockEntity> s = (List<StockEntity>) stock.findAll();
		return toInventory(s);
	}

	@Override
	public Inventory next(@RequestParam("id") Long id) {
		StockEntity s = stock.findOne(id);
		return toInventory(s);
	}

	@Override
	public List<Stock> update() {
		Iterable<StockEntity> i = stock.save(updatedStock());
		return i == null ? null
				: StreamSupport.stream(i.spliterator(), false).map(e -> convert(e)).collect(Collectors.toList());
	}

	private Stock convert(StockEntity e) {
		Stock s = new Stock();
		s.setId(e.getId());
		s.setItem(e.getItem());
		s.setBadQty(e.getBadQty());
		s.setGoodQty(e.getGoodQty());
		s.setSoldQty(e.getSoldQty());
		s.setPriceValue(e.getPriceValue());
		return s;
	}

	private BigDecimal avgDailySoldQty(StockEntity s) {
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

	private BigDecimal goodQty(StockEntity a) {
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

	private List<Inventory> toInventory(List<StockEntity> stocks) {
		return stocks.stream().map(s -> toInventory(s)).filter(i -> i.getDaysLevel() != 0).collect(toList());
	}

	private Inventory toInventory(StockEntity s) {
		if (s == null)
			return null;
		Inventory i = new Inventory();
		i.setId(s.getId());
		i.setItem(s.getItem());
		i.setGoodQty(goodQty(s));
		i.setBadQty(s.getBadQty());
		i.setAvgDailySoldQty(avgDailySoldQty(s));
		i.setDaysLevel(daysLevel(i));
		i.setValue(totalValue(s));
		i.setObsolesenceValue(obsolesenceValue(i));
		return i;
	}

	private Map<Long, BigDecimal> toItemSoldQtyMap(List<BillableDetailEntity> b) {
		return b.stream().collect(//
				groupingBy(BillableDetailEntity::getItemId, //
						mapping(BillableDetailEntity::getUnitQty, //
								reducing(ZERO, BigDecimal::add))));
	}

	private BigDecimal totalValue(StockEntity a) {
		price = a.getPriceValue() == null ? ZERO : a.getPriceValue();
		return goodQty(a).multiply(price);
	}

	private List<StockEntity> updatedStock() {
		List<BillableDetailEntity> b = detail.findByBillingOrderDateGreaterThanEqualOrderByItemAsc(sixtyDaysAgo());
		Map<Long, BigDecimal> m = toItemSoldQtyMap(b);
		List<StockEntity> s = (List<StockEntity>) stock.findAll();
		return updateStock(s, m);
	}

	private List<StockEntity> updateStock(List<StockEntity> stocks, Map<Long, BigDecimal> m) {
		stocks.forEach(s -> s.setSoldQty(m.get(s.getId())));
		return stocks;
	}
}