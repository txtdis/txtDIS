package ph.txtdis.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.CustomerVolumeDiscountEntity;
import ph.txtdis.dto.CustomerVolumeDiscount;
import ph.txtdis.repository.CustomerVolumeDiscountRepository;

@Service("customerVolumeDiscountService")
public class CustomerVolumeDiscountServiceImpl implements CustomerVolumeDiscountService {

	@Autowired
	private CustomerVolumeDiscountRepository repository;

	@Autowired
	private ItemService itemService;

	@Override
	public void updateDecisionData(String[] s) {
		updateDecisionData(repository, s);
	}

	@Override
	public List<CustomerVolumeDiscount> toList(List<CustomerVolumeDiscountEntity> l) {
		return l == null ? null : l.stream().map(e -> convert(e)).collect(Collectors.toList());
	}

	private CustomerVolumeDiscount convert(CustomerVolumeDiscountEntity e) {
		CustomerVolumeDiscount d = new CustomerVolumeDiscount();
		d.setId(e.getId());
		d.setItem(itemService.toDTO(e.getItem()));
		d.setIsValid(e.getIsValid());
		d.setTargetQty(e.getTargetQty());
		d.setDiscountValue(e.getDiscountValue());
		d.setRemarks(e.getRemarks());
		d.setStartDate(e.getStartDate());
		d.setCreatedBy(e.getCreatedBy());
		d.setCreatedOn(e.getCreatedOn());
		d.setDecidedBy(e.getDecidedBy());
		d.setDecidedOn(e.getDecidedOn());
		return d;
	}

	@Override
	public List<CustomerVolumeDiscountEntity> toEntities(List<CustomerVolumeDiscount> l) {
		return l == null ? null : l.stream().map(e -> convert(e)).collect(Collectors.toList());
	}

	private CustomerVolumeDiscountEntity convert(CustomerVolumeDiscount d) {
		CustomerVolumeDiscountEntity e = findSavedEntity(d);
		if (e == null)
			e = newEntity(d);
		if (decisionRecentlyMade(e, d))
			e.setDecidedOn(ZonedDateTime.now());
		e.setIsValid(d.getIsValid());
		e.setDecidedBy(d.getDecidedBy());
		e.setRemarks(d.getRemarks());
		return e;
	}

	private CustomerVolumeDiscountEntity findSavedEntity(CustomerVolumeDiscount d) {
		Long id = d.getId();
		return id == null ? null : repository.findOne(id);
	}

	private CustomerVolumeDiscountEntity newEntity(CustomerVolumeDiscount d) {
		CustomerVolumeDiscountEntity e = new CustomerVolumeDiscountEntity();
		e.setItem(itemService.toEntity(d.getItem()));
		e.setTargetQty(d.getTargetQty());
		e.setDiscountValue(d.getDiscountValue());
		e.setStartDate(d.getStartDate());
		return e;
	}

	private boolean decisionRecentlyMade(CustomerVolumeDiscountEntity e, CustomerVolumeDiscount d) {
		return (e.getDecidedBy() == null && d.getDecidedBy() != null) //
				|| (e.getIsValid() != null && e.getIsValid() == true //
						&& d.getIsValid() != null && d.getIsValid() == false);
	}
}
