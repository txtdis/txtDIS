package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.PickListEntity;
import ph.txtdis.dto.PickList;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.repository.PickListRepository;

@Service("loadReturnService")
public class LoadReturnServiceImpl implements LoadReturnService {

	@Autowired
	private PickListRepository repository;

	@Autowired
	private PickListService service;

	private PickList toDTO(PickListEntity e) {
		return service.toDTO(e);
	}

	@Override
	public PickList findById(Long id) throws NotFoundException {
		PickListEntity e = repository.findByIdAndReceivedOnNotNull(id);
		return toDTO(e);
	}

	@Override
	public PickList save(PickList t) {
		return service.save(t);
	}

	@Override
	public PickList first() {
		return firstSpun();
	}

	private PickList firstSpun() {
		PickListEntity e = repository.findFirstByReceivedOnNotNullOrderByIdAsc();
		return toDTO(e);
	}

	@Override
	public PickList firstToSpin() {
		return firstSpun();
	}

	@Override
	public PickList last() {
		return lastSpun();
	}

	private PickList lastSpun() {
		PickListEntity e = repository.findFirstByReceivedOnNotNullOrderByIdDesc();
		return toDTO(e);
	}

	@Override
	public PickList lastToSpin() {
		return lastSpun();
	}

	@Override
	public PickList next(Long id) {
		PickListEntity e = repository.findFirstByReceivedOnNotNullAndIdGreaterThanOrderByIdAsc(id);
		return toDTO(e);
	}

	@Override
	public PickList previous(Long id) {
		PickListEntity e = repository.findFirstByReceivedOnNotNullAndIdLessThanOrderByIdDesc(id);
		return toDTO(e);
	}
}