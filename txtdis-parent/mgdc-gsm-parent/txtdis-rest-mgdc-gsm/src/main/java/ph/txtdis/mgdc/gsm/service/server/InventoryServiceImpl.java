package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.Stock;
import ph.txtdis.mgdc.domain.StockEntity;
import ph.txtdis.mgdc.gsm.domain.BillableDetailEntity;
import ph.txtdis.mgdc.gsm.repository.BillingDetailRepository;
import ph.txtdis.mgdc.service.server.AbstractInventoryService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.*;
import static java.util.stream.StreamSupport.stream;

@Service("inventoryService")
public class InventoryServiceImpl //
	extends AbstractInventoryService {

	@Autowired
	private BillingDetailRepository detail;

	@Override
	public List<Stock> update() {
		Iterable<StockEntity> i = stockRepository.save(updatedStock());
		return i == null ? null : stream(i.spliterator(), false).map(e -> convert(e)).collect(Collectors.toList());
	}

	private List<StockEntity> updatedStock() {
		List<BillableDetailEntity> b = detail.findByBillingOrderDateGreaterThanEqualOrderByItemAsc(sixtyDaysAgo());
		Map<Long, BigDecimal> m = toItemSoldQtyMap(b);
		List<StockEntity> s = (List<StockEntity>) stockRepository.findAll();
		return updateStock(s, m);
	}

	private Map<Long, BigDecimal> toItemSoldQtyMap(List<BillableDetailEntity> b) {
		return b.stream().collect(//
			groupingBy(BillableDetailEntity::getItemId, //
				mapping(BillableDetailEntity::getInitialQty, //
					reducing(ZERO, BigDecimal::add))));
	}
}