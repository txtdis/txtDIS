package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.QualityType.BAD;
import static ph.txtdis.type.QualityType.GOOD;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Bom;
import ph.txtdis.dto.StockTakeVariance;
import ph.txtdis.mgdc.gsm.service.server.AbstractStockTakeVarianceService;
import ph.txtdis.mgdc.gsm.service.server.InventoryRestClientService;

@Service("stockTakeVarianceService")
public class StockTakeVarianceServiceImpl
	extends AbstractStockTakeVarianceService {

	private static final String IN = "in";

	private static final String OUT = "out";

	private static Logger logger = getLogger(StockTakeVarianceServiceImpl.class);

	@Autowired
	private InventoryRestClientService<Bom> inventoryRestClientService;

	private Map<String, StockTakeVariance> goodStockMap, badStockMap;

	@Override
	public List<StockTakeVariance> list(LocalDate countDate) {
		List<StockTakeVariance> variances = super.list(countDate);
		createGoodStockMap(variances);
		createBadStockMap(variances);
		addTransactionQty();
		return mapToList();
	}

	private void createGoodStockMap(List<StockTakeVariance> variances) {
		goodStockMap = new HashMap<>();
		variances.stream().filter(v -> v.getQuality() == GOOD).forEach(v -> goodStockMap.put(v.getItem(), v));
	}

	private void createBadStockMap(List<StockTakeVariance> variances) {
		badStockMap = new HashMap<>();
		variances.stream().filter(v -> v.getQuality() == BAD).forEach(v -> badStockMap.put(v.getItem(), v));
	}

	private void addTransactionQty() {
		addGoodIncomingQty();
		addBadIncomingQty();
		addGoodOutgoingQty();
		addBadOutgoingQty();
	}

	private void addGoodIncomingQty() {
		List<Bom> goodBoms = listBoms("GOOD", IN);
		logger.info("\n    GoodIncomingBoms: " + goodBoms);
		goodBoms.forEach(bom -> updateGoodStockMap(bom, IN));
	}

	private List<Bom> listBoms(String quality, String direction) {
		try {
			return inventoryRestClientService.module("bom").getList("/list?quality=" + quality + "&direction=" + direction
				+ "&start=" + startDate + "&end=" + latestCountDate);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void updateGoodStockMap(Bom bom, String direction) {
		String item = bom.getPart();
		StockTakeVariance variance = goodStockMap.get(item);
		if (variance != null)
			variance = setTransactionQty(bom, variance, direction);
		goodStockMap.put(item, variance);
		variance = goodStockMap.get(item);
		logger.info("\n    UpdatedTransactedGoodStockMap: " + variance);
	}

	private StockTakeVariance setTransactionQty(Bom bom, StockTakeVariance variance, String direction) {
		if (bom.getQty() != null)
			if (direction.equals(IN)) {
				variance.setInQty(variance.getInQty().add(bom.getQty()));
				logger.info("\n    ItemInQtyStockTakeVariance: " + variance);
			}
			else {
				variance.setOutQty(variance.getOutQty().add(bom.getQty()));
				logger.info("\n    ItemOutQtyStockTakeVariance: " + variance);
			}
		return variance;
	}

	private void addBadIncomingQty() {
		List<Bom> l = listBoms("BAD", IN);
		logger.info("\n    BadIncomingBoms: " + l);
		l.forEach(bom -> updateBadStockMap(bom, IN));
	}

	private void updateBadStockMap(Bom bom, String direction) {
		String item = bom.getPart();
		StockTakeVariance variance = badStockMap.get(item);
		if (variance != null)
			variance = setTransactionQty(bom, variance, direction);
		badStockMap.put(item, variance);
		variance = badStockMap.get(item);
		logger.info("\n    UpdatedTransactedBadStockMap: " + variance);
	}

	private void addGoodOutgoingQty() {
		List<Bom> l = listBoms("GOOD", OUT);
		logger.info("\n    GoodOutgoingBoms: " + l);
		l.forEach(bom -> updateGoodStockMap(bom, OUT));
	}

	private void addBadOutgoingQty() {
		List<Bom> l = listBoms("BAD", OUT);
		logger.info("\n    BadOutgoingBoms: " + l);
		l.forEach(bom -> updateBadStockMap(bom, OUT));
	}

	private List<StockTakeVariance> mapToList() {
		List<StockTakeVariance> l = new ArrayList<>(goodStockMap.values());
		l.addAll(badStockMap.values());
		return l.stream().filter(v -> v != null && areStartOrInOrOutOrFinalQtyPositive(v)).collect(Collectors.toList());
	}
}