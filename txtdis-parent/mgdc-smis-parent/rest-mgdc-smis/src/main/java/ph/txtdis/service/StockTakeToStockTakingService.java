package ph.txtdis.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.Item;
import ph.txtdis.domain.StockTaking;
import ph.txtdis.domain.StockTakingDetail;
import ph.txtdis.domain.User;
import ph.txtdis.domain.Warehouse;
import ph.txtdis.dto.StockTake;
import ph.txtdis.dto.StockTakeDetail;
import ph.txtdis.repository.ItemRepository;
import ph.txtdis.repository.StockTakeRepository;
import ph.txtdis.repository.UserRepository;
import ph.txtdis.repository.WarehouseRepository;

@Service("stockTakeToStockTakingService")
public class StockTakeToStockTakingService {

	@Autowired
	private ItemRepository itemRepo;

	@Autowired
	private StockTakeRepository stockTakeRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private WarehouseRepository warehouseRepo;

	public StockTaking toStockTaking(StockTake l) {
		return l == null ? null : convert(l);
	}

	private StockTaking convert(StockTake l) {
		return l.getId() == null ? create(l) : update(l);
	}

	private StockTaking create(StockTake e) {
		StockTaking i = new StockTaking();
		i.setWarehouse(toWarehouse(e));
		i.setChecker(toUser(e.getChecker()));
		i.setTaker(toUser(e.getTaker()));
		i.setStockTakeDate(e.getCountDate());
		i.setDetails(toStockTakingDetails(i, e));
		return i;
	}

	private Item toItem(StockTakeDetail ad) {
		return itemRepo.findOne(ad.getId());
	}

	private StockTakingDetail toStockTakingDetail(StockTaking i, StockTakeDetail ed) {
		StockTakingDetail id = new StockTakingDetail();
		id.setStockTaking(i);
		id.setItem(toItem(ed));
		id.setUom(ed.getUom());
		id.setQuality(ed.getQuality());
		id.setQty(ed.getQty());
		return id;
	}

	private List<StockTakingDetail> toStockTakingDetails(StockTaking i, StockTake e) {
		return e.getDetails().stream().map(ed -> toStockTakingDetail(i, ed)).collect(toList());
	}

	private User toUser(String n) {
		return n == null ? null : userRepo.findOne(n);
	}

	private Warehouse toWarehouse(StockTake s) {
		return s == null ? null : warehouseRepo.findByName(s.getWarehouse());
	}

	private StockTaking update(StockTake e) {
		StockTaking i = stockTakeRepo.findOne(e.getId());
		i.setDetails(toStockTakingDetails(i, e));
		return i;
	}
}
