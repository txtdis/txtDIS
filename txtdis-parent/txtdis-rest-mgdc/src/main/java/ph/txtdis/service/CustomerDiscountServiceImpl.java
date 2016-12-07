package ph.txtdis.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.CustomerDiscountEntity;
import ph.txtdis.dto.CustomerDiscount;
import ph.txtdis.repository.CustomerDiscountRepository;

@Service("customerDiscountService")
public class CustomerDiscountServiceImpl implements CustomerDiscountService {

	@Autowired
	private CustomerDiscountRepository repository;

	@Autowired
	private ItemService itemService;

	@Override
	public void updateDecisionData(String[] s) {
		updateDecisionData(repository, s);
	}

	@Override
	public List<CustomerDiscount> toList(List<CustomerDiscountEntity> l) {
		return l == null ? null : l.stream().map(e -> convert(e)).collect(Collectors.toList());
	}

	private CustomerDiscount convert(CustomerDiscountEntity e) {
		CustomerDiscount d = new CustomerDiscount();
		d.setId(e.getId());
		d.setItem(itemService.toDTO(e.getItem()));
		d.setIsValid(e.getIsValid());
		d.setLevel(e.getLevel());
		d.setDiscount(e.getPercent());
		d.setRemarks(e.getRemarks());
		d.setStartDate(e.getStartDate());
		d.setCreatedBy(e.getCreatedBy());
		d.setCreatedOn(e.getCreatedOn());
		d.setDecidedBy(e.getDecidedBy());
		d.setDecidedOn(e.getDecidedOn());
		return d;
	}

	@Override
	public List<CustomerDiscountEntity> toEntities(List<CustomerDiscount> l) {
		return l == null ? null : l.stream().map(e -> convert(e)).collect(Collectors.toList());
	}

	private CustomerDiscountEntity convert(CustomerDiscount d) {
		CustomerDiscountEntity e = findSavedEntity(d);
		if (e == null)
			e = newEntity(d);
		if (decisionRecentlyMade(e, d))
			e.setDecidedOn(ZonedDateTime.now());
		e.setIsValid(d.getIsValid());
		e.setDecidedBy(d.getDecidedBy());
		e.setRemarks(d.getRemarks());
		return e;
	}

	private CustomerDiscountEntity findSavedEntity(CustomerDiscount d) {
		Long id = d.getId();
		return id == null ? null : repository.findOne(id);
	}

	private CustomerDiscountEntity newEntity(CustomerDiscount d) {
		CustomerDiscountEntity e = new CustomerDiscountEntity();
		e.setItem(itemService.toEntity(d.getItem()));
		e.setLevel(d.getLevel());
		e.setPercent(d.getDiscount());
		e.setStartDate(d.getStartDate());
		return e;
	}

	private boolean decisionRecentlyMade(CustomerDiscountEntity e, CustomerDiscount d) {
		return (e.getDecidedBy() == null && d.getDecidedBy() != null) //
				|| (e.getIsValid() != null && e.getIsValid() == true //
						&& d.getIsValid() != null && d.getIsValid() == false);
	}
}
