package ph.txtdis.mgdc.ccbpi.service.server;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.PickList;
import ph.txtdis.dto.PickListDetail;
import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;
import ph.txtdis.mgdc.ccbpi.domain.PickListDetailEntity;
import ph.txtdis.mgdc.ccbpi.domain.PickListEntity;
import ph.txtdis.mgdc.ccbpi.repository.PickListRepository;

@Service("loadReturnService")
public class LoadReturnServiceImpl
		implements LoadReturnService {

	@Autowired
	private PickListRepository repository;

	@Autowired
	private ItemService itemService;

	@Autowired
	private PickListService service;

	@Override
	public PickList findByPrimaryKey(Long id) {
		PickListEntity e = findEntityByPrimaryKey(id);
		return toDTO(e);
	}

	@Override
	public PickListEntity findEntityByPrimaryKey(Long id) {
		return repository.findByReceivedOnNotNullAndId(id);
	}

	private PickList toDTO(PickListEntity e) {
		return service.toModel(e);
	}

	@Override
	public PickList findByReferenceId(Long id) {
		return findByPrimaryKey(id);
	}

	@Override
	public List<PickListEntity> post(List<PickListEntity> e) {
		Iterable<PickListEntity> i = repository.save(e);
		return stream(i.spliterator(), false).collect(toList());
	}

	@Override
	public PickListEntity post(PickListEntity e) {

		return repository.save(e);
	}

	@Override
	public PickList save(PickList p) {
		PickListEntity e = service.findEntityByPrimaryKey(p.getId());
		if (p.getBookings() == null)
			e.setBillings(unpickPickedBillings(e));
		e.setReceivedBy(p.getReceivedBy());
		e.setReceivedOn(ZonedDateTime.now());
		e.setDetails(toDetails(e, p));
		e = post(e);
		return toDTO(e);
	}

	private List<BillableEntity> unpickPickedBillings(PickListEntity e) {
		List<BillableEntity> l = e.getBillings();
		return l == null ? null : l.stream().map(b -> unpickBilling(b)).collect(Collectors.toList());
	}

	private BillableEntity unpickBilling(BillableEntity e) {
		e.setPicking(null);
		return e;
	}

	private List<PickListDetailEntity> toDetails(PickListEntity e, PickList p) {
		Map<Long, PickListDetailEntity> m = new HashMap<>();
		for (PickListDetailEntity ed : e.getDetails())
			m.put(ed.getItem().getId(), ed);
		for (PickListDetail pd : p.getDetails())
			setDetail(m, pd);
		return new ArrayList<>(m.values());
	}

	private void setDetail(Map<Long, PickListDetailEntity> m, PickListDetail pd) {
		Long id = pd.getId();
		PickListDetailEntity ed = m.get(id);
		m.put(id, ed == null ? toEntity(pd) : setReturnedQty(ed, pd));
	}

	private PickListDetailEntity toEntity(PickListDetail pd) {
		PickListDetailEntity ed = new PickListDetailEntity();
		ed.setItem(itemService.findEntityByPrimaryKey(pd.getId()));
		return setReturnedQty(ed, pd);
	}

	private PickListDetailEntity setReturnedQty(PickListDetailEntity ed, PickListDetail pd) {
		ed.setReturnedCount(pd.getReturnedQty().intValue());
		return ed;
	}

	@Override
	public PickList next(Long id) {
		return toDTO(id == null ? firstEntity() : nextEntity(id));
	}

	private PickListEntity firstEntity() {
		return repository.findFirstByReceivedOnNotNullOrderByIdAsc();
	}

	protected PickListEntity nextEntity(Long id) {
		return repository.findFirstByReceivedOnNotNullAndIdGreaterThanOrderByIdAsc(id);
	}

	@Override
	public PickList previous(Long id) {
		return toDTO(id == null ? lastEntity() : previousEntity(id));
	}

	protected PickListEntity lastEntity() {
		return repository.findFirstByReceivedOnNotNullOrderByIdDesc();
	}

	protected PickListEntity previousEntity(Long id) {
		return repository.findFirstByReceivedOnNotNullAndIdLessThanOrderByIdDesc(id);
	}
}