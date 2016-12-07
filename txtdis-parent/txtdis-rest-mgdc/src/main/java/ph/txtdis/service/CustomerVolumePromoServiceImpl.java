package ph.txtdis.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.CustomerVolumePromoEntity;
import ph.txtdis.dto.CustomerVolumePromo;
import ph.txtdis.repository.CustomerVolumePromoRepository;

@Service("customerVolumePromoService")
public class CustomerVolumePromoServiceImpl implements CustomerVolumePromoService {

	@Autowired
	private CustomerVolumePromoRepository repository;

	@Autowired
	private ItemService itemService;

	@Override
	public void updateDecisionData(String[] s) {
		updateDecisionData(repository, s);
	}

	@Override
	public List<CustomerVolumePromo> toList(List<CustomerVolumePromoEntity> l) {
		return l == null ? null : l.stream().map(e -> convert(e)).collect(Collectors.toList());
	}

	private CustomerVolumePromo convert(CustomerVolumePromoEntity e) {
		CustomerVolumePromo d = new CustomerVolumePromo();
		d.setId(e.getId());
		d.setItem(itemService.toDTO(e.getItem()));
		d.setIsValid(e.getIsValid());
		d.setTargetQty(e.getTargetQty());
		d.setFreeQty(e.getFreeQty());
		d.setRemarks(e.getRemarks());
		d.setStartDate(e.getStartDate());
		d.setCreatedBy(e.getCreatedBy());
		d.setCreatedOn(e.getCreatedOn());
		d.setDecidedBy(e.getDecidedBy());
		d.setDecidedOn(e.getDecidedOn());
		return d;
	}

	@Override
	public List<CustomerVolumePromoEntity> toEntities(List<CustomerVolumePromo> l) {
		return l == null ? null : l.stream().map(e -> convert(e)).collect(Collectors.toList());
	}

	private CustomerVolumePromoEntity convert(CustomerVolumePromo d) {
		CustomerVolumePromoEntity e = findSavedEntity(d);
		if (e == null)
			e = newEntity(d);
		if (decisionRecentlyMade(e, d))
			e.setDecidedOn(ZonedDateTime.now());
		e.setIsValid(d.getIsValid());
		e.setDecidedBy(d.getDecidedBy());
		e.setRemarks(d.getRemarks());
		return e;
	}

	private CustomerVolumePromoEntity findSavedEntity(CustomerVolumePromo d) {
		Long id = d.getId();
		return id == null ? null : repository.findOne(id);
	}

	private CustomerVolumePromoEntity newEntity(CustomerVolumePromo d) {
		CustomerVolumePromoEntity e = new CustomerVolumePromoEntity();
		e.setItem(itemService.toEntity(d.getItem()));
		e.setTargetQty(d.getTargetQty());
		e.setFreeQty(d.getFreeQty());
		e.setStartDate(d.getStartDate());
		return e;
	}

	private boolean decisionRecentlyMade(CustomerVolumePromoEntity e, CustomerVolumePromo d) {
		return (e.getDecidedBy() == null && d.getDecidedBy() != null) //
				|| (e.getIsValid() != null && e.getIsValid() == true //
						&& d.getIsValid() != null && d.getIsValid() == false);
	}
}
