package ph.txtdis.mgdc.gsm.service.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.CreditDetail;
import ph.txtdis.mgdc.gsm.domain.CreditDetailEntity;
import ph.txtdis.mgdc.gsm.domain.CustomerEntity;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.gsm.repository.CreditDetailRepository;

@Service("creditDetailService")
public class CreditDetailServiceImpl implements CreditDetailService {

	@Autowired
	private CreditDetailRepository repository;

	@Override
	public List<CreditDetailEntity> getNewAndOldCreditDetails(CustomerEntity e, Customer c) {
		List<CreditDetailEntity> l = new ArrayList<>(e.getCreditDetails());
		l.addAll(toEntities(newCreditDetailsNeedingApproval(c)));
		return l;
	}

	private List<CreditDetail> newCreditDetailsNeedingApproval(Customer c) {
		return c.getCreditDetails().stream().filter(p -> p.getIsValid() == null).collect(Collectors.toList());
	}

	@Override
	public List<CreditDetailEntity> getUpdatedCreditDetailDecisions(CustomerEntity e, Customer c) {
		return e.getCreditDetails().stream().map(d -> updateNewCreditDetailDecisions(d, c)).collect(Collectors.toList());
	}

	private CreditDetailEntity updateNewCreditDetailDecisions(CreditDetailEntity e, Customer c) {
		Optional<CreditDetail> o = c.getCreditDetails().stream() //
				.filter(t -> e.getStartDate().isEqual(t.getStartDate())) //
				.findAny();
		return o.isPresent() && e.getIsValid() == null ? setDecisionData(e, o.get()) : e;
	}

	@Override
	public boolean hasDecisionOnNewCreditDetailsBeenMade(CustomerEntity e, Customer c) {
		return isDecisionOnAnANewCreditDetailEntityNeeded(e) && hasDecisionOnANewCreditDetailBeenMade(c);
	}

	private boolean isDecisionOnAnANewCreditDetailEntityNeeded(CustomerEntity e) {
		return e.getCreditDetails().stream().anyMatch(p -> p.getIsValid() == null);
	}

	private boolean hasDecisionOnANewCreditDetailBeenMade(Customer c) {
		return newCreditDetailsNeedingApproval(c).isEmpty();
	}

	@Override
	public List<CreditDetailEntity> toEntities(List<CreditDetail> l) {
		return l == null ? null : l.stream().map(t -> toEntity(t)).collect(Collectors.toList());
	}

	private CreditDetailEntity toEntity(CreditDetail c) {
		CreditDetailEntity e = findSavedEntity(c);
		if (e == null)
			e = newEntity(c);
		return setDecisionData(e, c);
	}

	private CreditDetailEntity setDecisionData(CreditDetailEntity e, CreditDetail c) {
		e.setIsValid(c.getIsValid());
		e.setRemarks(c.getRemarks());
		e.setDecidedBy(c.getDecidedBy());
		e.setDecidedOn(c.getDecidedOn());
		return e;
	}

	private CreditDetailEntity findSavedEntity(CreditDetail t) {
		Long id = t.getId();
		return id == null ? null : repository.findOne(t.getId());
	}

	private CreditDetailEntity newEntity(CreditDetail t) {
		CreditDetailEntity e = new CreditDetailEntity();
		e.setCreditLimit(t.getCreditLimit());
		e.setGracePeriodInDays(t.getGracePeriodInDays());
		e.setStartDate(t.getStartDate());
		e.setTermInDays(t.getTermInDays());
		return e;
	}

	@Override
	public List<CreditDetail> toList(List<CreditDetailEntity> l) {
		return l == null ? null : l.stream().map(e -> toDTO(e)).collect(Collectors.toList());
	}

	private CreditDetail toDTO(CreditDetailEntity e) {
		if (e == null)
			return null;
		CreditDetail d = new CreditDetail();
		d.setId(e.getId());
		d.setCreditLimit(e.getCreditLimit());
		d.setGracePeriodInDays(e.getGracePeriodInDays());
		d.setIsValid(e.getIsValid());
		d.setRemarks(e.getRemarks());
		d.setStartDate(e.getStartDate());
		d.setTermInDays(e.getTermInDays());
		d.setCreatedBy(e.getCreatedBy());
		d.setCreatedOn(e.getCreatedOn());
		d.setDecidedBy(e.getDecidedBy());
		d.setDecidedOn(e.getDecidedOn());
		return d;
	}

	@Override
	public void updateDecisionData(String[] s) {
		updateDecisionData(repository, s);
	}
}
