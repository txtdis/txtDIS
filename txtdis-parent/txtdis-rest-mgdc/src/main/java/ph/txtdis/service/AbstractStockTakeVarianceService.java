package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.QualityType.BAD;
import static ph.txtdis.type.QualityType.GOOD;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.domain.BomEntity;
import ph.txtdis.domain.ItemEntity;
import ph.txtdis.domain.StockTakeDetailEntity;
import ph.txtdis.domain.StockTakeEntity;
import ph.txtdis.domain.StockTakeVarianceEntity;
import ph.txtdis.dto.StockTakeVariance;
import ph.txtdis.repository.StockTakeVarianceRepository;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.TransactionDirectionType;
import ph.txtdis.type.UomType;
import ph.txtdis.util.NumberUtils;

public abstract class AbstractStockTakeVarianceService implements StockTakeVarianceService {

	private static Logger logger = getLogger(AbstractStockTakeVarianceService.class);

	private static final long LATEST = 0;

	private static final long PREVIOUS = 1;

	@Autowired
	private BillableService billableService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private StockTakeService stockTakeService;

	@Autowired
	private StockTakeVarianceRepository varianceRepository;

	private LocalDate previousCountDate;

	private Map<ItemEntity, StockTakeVarianceEntity> goodStockEntityMap, badStockEntityMap;

	protected LocalDate startDate, latestCountDate;

	@Override
	public List<StockTakeVariance> list(LocalDate countDate) {
		latestCountDate = countDate;
		List<StockTakeVariance> l = getList();
		return l != null ? l : createList();
	}

	private List<StockTakeVariance> getList() {
		List<StockTakeVarianceEntity> l = varianceRepository.findByCountDate(latestCountDate);
		return l == null || l.isEmpty() ? null : toList(l);
	}

	private List<StockTakeVariance> createList() {
		createStockMaps(itemAndQualityOnlyStockTakeVariances());
		addStockTakeData();
		startDate = previousCountDate.plusDays(1L);
		addIncomingData();
		addOutgoingData();
		return entityMapToList();
	}

	private void addStockTakeData() {
		addStockTakeData(LATEST);
		addStockTakeData(PREVIOUS);
	}

	private void createStockMaps(List<StockTakeVarianceEntity> variances) {
		createGoodStockEntityMap(variances);
		createBadStockEntityMap(variances);
	}

	private void createGoodStockEntityMap(List<StockTakeVarianceEntity> variances) {
		goodStockEntityMap = new HashMap<>();
		variances.stream().filter(v -> v.getQuality() == GOOD).forEach(v -> goodStockEntityMap.put(v.getItem(), v));
	}

	private void createBadStockEntityMap(List<StockTakeVarianceEntity> variances) {
		badStockEntityMap = new HashMap<>();
		variances.stream().filter(v -> v.getQuality() == BAD).forEach(v -> badStockEntityMap.put(v.getItem(), v));
	}

	private List<StockTakeVarianceEntity> itemAndQualityOnlyStockTakeVariances() {
		List<StockTakeVarianceEntity> variances = new ArrayList<>();
		List<ItemEntity> items = itemService.listEntities();
		items.forEach(i -> variances.addAll(itemAndQualityOnlyStockTakeVarianceEntity(i)));
		return variances;
	}

	private List<StockTakeVarianceEntity> itemAndQualityOnlyStockTakeVarianceEntity(ItemEntity i) {
		return Arrays.asList( //
				goodItemOnlyStockTakeVarianceEntity(i), //
				badItemOnlyStockTakeVarianceEntity(i));
	}

	private StockTakeVarianceEntity goodItemOnlyStockTakeVarianceEntity(ItemEntity i) {
		StockTakeVarianceEntity e = itemOnlyStockTakeVarianceEntity(i);
		e.setQuality(QualityType.GOOD);
		logger.info("\n    GoodItemOnlyStockTakeVarianceEntity: " + e);
		return e;
	}

	private StockTakeVarianceEntity itemOnlyStockTakeVarianceEntity(ItemEntity i) {
		StockTakeVarianceEntity e = new StockTakeVarianceEntity();
		e.setCountDate(latestCountDate);
		e.setItem(i);
		e.setUom(UomType.CS);
		return e;
	}

	private StockTakeVarianceEntity badItemOnlyStockTakeVarianceEntity(ItemEntity i) {
		StockTakeVarianceEntity e = itemOnlyStockTakeVarianceEntity(i);
		e.setQuality(QualityType.BAD);
		logger.info("\n    BadItemOnlyStockTakeVarianceEntity: " + e);
		return e;
	}

	private void addStockTakeData(long daysOffset) {
		StockTakeEntity e = stockTakeService.findLatestEntityOnOrBeforeCutoff(latestCountDate.minusDays(daysOffset));
		setCountDate(e, daysOffset);
		e.getDetails().forEach(detail -> updateStockMap(detail, daysOffset));
	}

	private void setCountDate(StockTakeEntity e, long daysOffset) {
		if (daysOffset == LATEST)
			latestCountDate = e.getStockTakeDate();
		else
			previousCountDate = e.getStockTakeDate();
	}

	private void updateStockMap(StockTakeDetailEntity detail, long daysOffset) {
		if (detail.getQuality() == GOOD)
			updateGoodStockMap(detail, daysOffset);
		else
			updateBadStockMap(detail, daysOffset);
	}

	private void updateGoodStockMap(StockTakeDetailEntity detail, long cutoff) {
		ItemEntity item = detail.getItem();
		StockTakeVarianceEntity variance = goodStockEntityMap.get(item);
		if (variance != null)
			variance = setCountQty(detail, variance, cutoff);
		goodStockEntityMap.put(item, variance);
		variance = goodStockEntityMap.get(item);
		logger.info("\n    UpdateCountedGoodStockEntityMap: " + variance);
	}

	private StockTakeVarianceEntity setCountQty(StockTakeDetailEntity detail, StockTakeVarianceEntity variance,
			long cutoff) {
		if (cutoff == LATEST) {
			variance.setActualQty(detail.getQty());
			logger.info("\n    ItemActualQtyStockTakeVarianceEntity: " + variance);
		} else {
			variance.setStartQty(detail.getQty());
			logger.info("\n    ItemStartQtyStockTakeVarianceEntity: " + variance);
		}
		return variance;
	}

	private void updateBadStockMap(StockTakeDetailEntity detail, long cutoff) {
		ItemEntity item = detail.getItem();
		StockTakeVarianceEntity variance = badStockEntityMap.get(item);
		if (variance != null)
			variance = setCountQty(detail, variance, cutoff);
		badStockEntityMap.put(item, variance);
		variance = badStockEntityMap.get(item);
		logger.info("\n    UpdatedCountedBadStockEntityMap: " + variance);
	}

	private void addIncomingData() {
		addGoodItemIncomingQty();
		addBadItemIncomingQty();
	}

	private void addGoodItemIncomingQty() {
		List<BomEntity> goodItems = billableService.listGoodItemsIncomingQty(startDate, latestCountDate);
		goodItems.forEach(bom -> updateGoodStockMap(bom, TransactionDirectionType.INCOMING));
	}

	private void updateGoodStockMap(BomEntity bom, TransactionDirectionType direction) {
		ItemEntity item = bom.getPart();
		StockTakeVarianceEntity variance = goodStockEntityMap.get(item);
		if (variance != null)
			variance = setTransactionQty(bom, variance, direction);
		goodStockEntityMap.put(item, variance);
		variance = goodStockEntityMap.get(item);
		logger.info("\n    UpdatedTransactedGoodStockEntityMap: " + variance);
	}

	private StockTakeVarianceEntity setTransactionQty(BomEntity bom, StockTakeVarianceEntity variance,
			TransactionDirectionType direction) {
		if (direction == TransactionDirectionType.INCOMING) {
			variance.setInQty(bom.getQty());
			logger.info("\n    ItemInQtyStockTakeVarianceEntity: " + variance);
		} else {
			variance.setOutQty(bom.getQty());
			logger.info("\n    ItemOutQtyStockTakeVarianceEntity: " + variance);
		}
		return variance;
	}

	private void addBadItemIncomingQty() {
		List<BomEntity> badItems = billableService.listBadItemsIncomingQty(startDate, latestCountDate);
		badItems.forEach(bom -> updateBadStockMap(bom, TransactionDirectionType.INCOMING));
	}

	private void updateBadStockMap(BomEntity bom, TransactionDirectionType direction) {
		ItemEntity item = bom.getPart();
		StockTakeVarianceEntity variance = badStockEntityMap.get(item);
		if (variance != null)
			variance = setTransactionQty(bom, variance, direction);
		badStockEntityMap.put(item, variance);
		variance = badStockEntityMap.get(item);
		logger.info("\n    UpdatedTransactedBadStockEntityMap: " + variance);
	}

	private void addOutgoingData() {
		addGoodItemOutgoingQty();
		addBadItemOutgoingQty();
	}

	private void addGoodItemOutgoingQty() {
		List<BomEntity> goodItems = billableService.listGoodItemsOutgoingQty(startDate, latestCountDate);
		goodItems.forEach(bom -> updateGoodStockMap(bom, TransactionDirectionType.OUTGOING));
	}

	private void addBadItemOutgoingQty() {
		List<BomEntity> badItems = billableService.listBadItemsOutgoingQty(startDate, latestCountDate);
		badItems.forEach(bom -> updateBadStockMap(bom, TransactionDirectionType.OUTGOING));
	}

	private List<StockTakeVariance> entityMapToList() {
		List<StockTakeVarianceEntity> l = new ArrayList<>(goodStockEntityMap.values());
		l.addAll(badStockEntityMap.values());
		return toList(l);
	}

	private List<StockTakeVariance> toList(List<StockTakeVarianceEntity> l) {
		return l == null ? null : l.stream().map(e -> toDTO(e)).collect(Collectors.toList());
	}

	private StockTakeVariance toDTO(StockTakeVarianceEntity e) {
		StockTakeVariance v = new StockTakeVariance();
		ItemEntity item = e.getItem();
		v.setId(item.getId());
		v.setItem(item.getName());
		v.setQuality(e.getQuality());
		v.setUom(e.getUom());
		v.setQtyPerCase(itemService.getQtyPerCase(item));
		v.setStartQty(e.getStartQty());
		v.setInQty(e.getInQty());
		v.setOutQty(e.getOutQty());
		v.setActualQty(e.getActualQty());
		v.setFinalQty(e.getFinalQty());
		v.setJustification(e.getJustification());
		v.setCountDate(e.getCountDate());
		logger.info("\n    StockTakeVariance: " + v);
		return v;
	}

	protected boolean areStartOrInOrOutOrFinalQtyPositive(StockTakeVariance e) {
		return NumberUtils.isPositive(e.getStartQty()) || NumberUtils.isPositive(e.getInQty())
				|| NumberUtils.isPositive(e.getOutQty()) || NumberUtils.isPositive(e.getActualQty())
				|| NumberUtils.isPositive(e.getFinalQty());
	}
}