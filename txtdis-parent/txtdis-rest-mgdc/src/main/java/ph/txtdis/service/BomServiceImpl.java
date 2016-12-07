package ph.txtdis.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.BillableDetailEntity;
import ph.txtdis.domain.BomEntity;
import ph.txtdis.dto.Bom;
import ph.txtdis.repository.BomRepository;

@Service("bomService")
public class BomServiceImpl implements BomService {

	@Autowired
	private BillableService billableService;

	@Autowired
	private BomRepository repository;

	@Autowired
	private ItemService itemService;

	@Override
	public List<Bom> getBadIncomingList(LocalDate start, LocalDate end) {
		List<BomEntity> l = billableService.listBadItemsIncomingQty(start, end);
		return toList(l);
	}

	@Override
	public List<Bom> getBadOutgoingList(LocalDate start, LocalDate end) {
		List<BomEntity> l = billableService.listBadItemsOutgoingQty(start, end);
		return toList(l);
	}

	@Override
	public List<Bom> getGoodIncomingList(LocalDate start, LocalDate end) {
		List<BomEntity> l = billableService.listGoodItemsIncomingQty(start, end);
		return toList(l);
	}

	@Override
	public List<Bom> getGoodOutgoingList(LocalDate start, LocalDate end) {
		List<BomEntity> l = billableService.listGoodItemsOutgoingQty(start, end);
		return toList(l);
	}

	@Override
	public List<BomEntity> listBomEntities(BillableDetailEntity d) {
		return repository.findByItem(d.getItem());
	}

	@Override
	public List<Bom> toList(List<BomEntity> l) {
		return l == null ? null : l.stream().map(e -> toDTO(e)).collect(Collectors.toList());
	}

	private Bom toDTO(BomEntity e) {
		Bom b = new Bom();
		b.setId(e.getPart().getId());
		b.setPart(e.getPart().getName());
		b.setQty(e.getQty());
		b.setCreatedBy(e.getCreatedBy());
		b.setCreatedOn(e.getCreatedOn());
		return b;
	}

	@Override
	public List<BomEntity> toEntities(List<Bom> l) {
		return l == null ? null : l.stream().map(b -> toEntity(b)).collect(Collectors.toList());
	}

	private BomEntity toEntity(Bom b) {
		BomEntity e = new BomEntity();
		e.setPart(itemService.toEntity(itemService.findByName(b.getPart())));
		e.setQty(b.getQty());
		return e;
	}
}