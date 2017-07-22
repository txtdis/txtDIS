package ph.txtdis.mgdc.ccbpi.service.server;

import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.UserEntity;
import ph.txtdis.dto.StockTake;
import ph.txtdis.dto.StockTakeDetail;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.mgdc.ccbpi.domain.ItemEntity;
import ph.txtdis.mgdc.ccbpi.domain.StockTakeDetailEntity;
import ph.txtdis.mgdc.ccbpi.domain.StockTakeEntity;
import ph.txtdis.mgdc.ccbpi.repository.StockTakeRepository;
import ph.txtdis.mgdc.domain.WarehouseEntity;
import ph.txtdis.mgdc.repository.WarehouseRepository;
import ph.txtdis.repository.UserRepository;
import ph.txtdis.type.UomType;

@Service("stockTakeService")
public class StockTakeServiceImpl //
		implements StockTakeService {

	@Autowired
	private ItemService itemService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private WarehouseRepository warehouseRepository;

	@Autowired
	private StockTakeRepository stockTakeRepository;

	@Override
	public StockTake find(Long id) {
		StockTakeEntity i = stockTakeRepository.findOne(id);
		return toStockTake(i);
	}

	@Override
	public StockTake findByDate(LocalDate d) throws Exception {
		StockTakeEntity i = findLatestEntityOnOrBeforeCutoff(d);
		if (i == null)
			throw new NotFoundException("Stock take on " + toDateDisplay(d));
		return toStockTake(i);
	}

	@Override
	public StockTake findByWarehouseAndDate(String s, LocalDate d) {
		StockTakeEntity i = findEntityByWarehouseAndDate(s, d);
		return toStockTake(i);
	}

	private StockTakeEntity findEntityByWarehouseAndDate(String s, LocalDate d) {
		if (s.equals("all"))
			return findLatestEntityOnOrBeforeCutoff(d);
		return stockTakeRepository.findFirstByWarehouseNameAndStockTakeDate(s, d);
	}

	@Override
	public StockTakeEntity findEntityByCountDate(LocalDate d) {
		return stockTakeRepository.findByStockTakeDate(d);
	}

	@Override
	public StockTakeEntity findLatestEntityOnOrBeforeCutoff(LocalDate d) {
		return stockTakeRepository.findFirstByStockTakeDateLessThanEqualOrderByStockTakeDateDesc(d);
	}

	@Override
	public StockTake first() {
		StockTakeEntity i = stockTakeRepository.findFirstByOrderByIdAsc();
		return toStockTake(i);
	}

	@Override
	public StockTake firstToSpin() {
		StockTakeEntity i = firstSpun();
		return spunIdOnlyStockTake(i);
	}

	@Override
	public StockTake last() {
		StockTakeEntity i = stockTakeRepository.findFirstByOrderByIdDesc();
		return toStockTake(i);
	}

	@Override
	public StockTake lastToSpin() {
		StockTakeEntity i = lastSpun();
		return spunIdOnlyStockTake(i);
	}

	@Override
	public StockTake next(Long id) {
		StockTakeEntity i = stockTakeRepository.findFirstByIdGreaterThanOrderByIdAsc(id);
		return toStockTake(i);
	}

	@Override
	public StockTake previous(Long id) {
		StockTakeEntity i = stockTakeRepository.findFirstByIdLessThanOrderByIdDesc(id);
		return toStockTake(i);
	}

	@Override
	public StockTake save(StockTake a) {
		StockTakeEntity i = toStockTaking(a);
		i = stockTakeRepository.save(i);
		return toStockTake(i);
	}

	private StockTakeEntity firstSpun() {
		return stockTakeRepository.findFirstByOrderByIdAsc();
	}

	private StockTakeEntity lastSpun() {
		return stockTakeRepository.findFirstByOrderByIdDesc();
	}

	private StockTake spunIdOnlyStockTake(StockTakeEntity i) {
		StockTake a = new StockTake();
		a.setId(i.getId());
		return a;
	}

	public List<StockTake> toStockTake(List<StockTakeEntity> p) {
		return p == null ? null : convert(p);
	}

	public StockTake toStockTake(StockTakeEntity p) {
		return p == null ? null : convert(p);
	}

	private List<StockTake> convert(List<StockTakeEntity> l) {
		return l.stream().map(p -> convert(p)).collect(toList());
	}

	private StockTake convert(StockTakeEntity e) {
		StockTake s = new StockTake();
		s.setId(e.getId());
		s.setCountDate(e.getStockTakeDate());
		s.setWarehouse(toName(e.getWarehouse()));
		s.setChecker(toName(e.getChecker()));
		s.setTaker(toName(e.getTaker()));
		s.setDetails(toStockTakeDetails(e));
		s.setCreatedBy(e.getCreatedBy());
		s.setCreatedOn(e.getCreatedOn());
		return s;
	}

	private String toName(UserEntity user) {
		return user == null ? null : user.getName();
	}

	private String toName(WarehouseEntity u) {
		return u == null ? null : u.getName();
	}

	private StockTakeDetail toStockTakeDetail(StockTakeDetailEntity e) {
		StockTakeDetail d = new StockTakeDetail();
		ItemEntity i = e.getItem();
		d.setId(i.getId());
		d.setName(i.getName());
		d.setQty(e.getQty());
		d.setQuality(e.getQuality());
		d.setUom(e.getUom());
		d.setQtyPerCase(getQtyPerCase(e));
		return d;
	}

	private int getQtyPerCase(StockTakeDetailEntity e) {
		if (e.getUom() != UomType.CS)
			return 0;
		return itemService.getCountPerCase(e.getItem());
	}

	private List<StockTakeDetail> toStockTakeDetails(StockTakeEntity i) {
		return i.getDetails().stream().map(id -> toStockTakeDetail(id)).collect(toList());
	}

	public StockTakeEntity toStockTaking(StockTake l) {
		return l == null ? null : create(l);
	}

	private StockTakeEntity create(StockTake s) {
		StockTakeEntity e = new StockTakeEntity();
		e.setWarehouse(toWarehouse(s));
		e.setChecker(toUser(s.getChecker()));
		e.setTaker(toUser(s.getTaker()));
		e.setStockTakeDate(s.getCountDate());
		e.setDetails(toStockTakingDetails(e, s));
		return e;
	}

	private ItemEntity toItem(StockTakeDetail ad) {
		return itemService.findEntityByPrimaryKey(ad.getId());
	}

	protected List<StockTakeDetailEntity> toStockTakingDetails(StockTakeEntity i, StockTake e) {
		return e.getDetails().stream().map(ed -> toEntityDetail(i, ed)).collect(Collectors.toList());
	}

	private StockTakeDetailEntity toEntityDetail(StockTakeEntity e, StockTakeDetail d) {
		StockTakeDetailEntity ed = new StockTakeDetailEntity();
		ed.setStockTaking(e);
		ed.setItem(toItem(d));
		ed.setUom(d.getUom());
		ed.setQuality(d.getQuality());
		ed.setQty(d.getQty());
		return ed;
	}

	private UserEntity toUser(String n) {
		return n == null ? null : userRepository.findOne(n);
	}

	private WarehouseEntity toWarehouse(StockTake s) {
		return s == null ? null : warehouseRepository.findByNameIgnoreCase(s.getWarehouse());
	}
}