package ph.txtdis.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.stereotype.Service;

import ph.txtdis.domain.Item;
import ph.txtdis.domain.StockTaking;
import ph.txtdis.domain.StockTakingDetail;
import ph.txtdis.domain.User;
import ph.txtdis.domain.Warehouse;
import ph.txtdis.dto.StockTake;
import ph.txtdis.dto.StockTakeDetail;

@Service("stockTakingToStockTakeService")
public class StockTakingToStockTakeService {

	public List<StockTake> toStockTake(List<StockTaking> p) {
		return p == null ? null : convert(p);
	}

	public StockTake toStockTake(StockTaking p) {
		return p == null ? null : convert(p);
	}

	private List<StockTake> convert(List<StockTaking> l) {
		return l.stream().map(p -> convert(p)).collect(toList());
	}

	private StockTake convert(StockTaking i) {
		StockTake a = new StockTake();
		a.setId(i.getId());
		a.setCountDate(i.getStockTakeDate());
		a.setWarehouse(toName(i.getWarehouse()));
		a.setChecker(toName(i.getChecker()));
		a.setTaker(toName(i.getTaker()));
		a.setDetails(toStockTakeDetails(i));
		a.setCreatedBy(i.getCreatedBy());
		a.setCreatedOn(i.getCreatedOn());
		return a;
	}

	private String toName(User u) {
		return u == null ? null : u.getUsername();
	}

	private String toName(Warehouse u) {
		return u == null ? null : u.getName();
	}

	private StockTakeDetail toStockTakeDetail(StockTakingDetail id) {
		StockTakeDetail ad = new StockTakeDetail();
		Item i = id.getItem();
		ad.setId(i.getId());
		ad.setName(i.getName());
		ad.setQty(id.getQty());
		ad.setQuality(id.getQuality());
		ad.setUom(id.getUom());
		return ad;
	}

	private List<StockTakeDetail> toStockTakeDetails(StockTaking i) {
		return i.getDetails().stream().map(id -> toStockTakeDetail(id)).collect(toList());
	}
}
