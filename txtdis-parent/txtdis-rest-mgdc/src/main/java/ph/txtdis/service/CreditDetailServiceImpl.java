package ph.txtdis.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.CreditDetailEntity;
import ph.txtdis.dto.CreditDetail;
import ph.txtdis.repository.CreditDetailRepository;

@Service("creditDetailService")
public class CreditDetailServiceImpl implements CreditDetailService {

	@Autowired
	private CreditDetailRepository repository;

	@Override
	public void updateDecisionData(String[] s) {
		updateDecisionData(repository, s);
	}

	@Override
	public List<CreditDetail> toList(List<CreditDetailEntity> l) {
		return l == null ? null : l.stream().map(e -> convert(e)).collect(Collectors.toList());
	}

	private CreditDetail convert(CreditDetailEntity e) {
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
	public List<CreditDetailEntity> toEntities(List<CreditDetail> l) {
		return l == null ? null : l.stream().map(t -> convert(t)).collect(Collectors.toList());
	}

	private CreditDetailEntity convert(CreditDetail t) {
		CreditDetailEntity e = findSavedEntity(t);
		if (e == null)
			e = newEntity(t);
		if ((t.getDecidedBy() != null && e.getDecidedBy() == null) //
				|| (e.getIsValid() != null && e.getIsValid() == true //
						&& t.getIsValid() != null && t.getIsValid() == false))
			e.setDecidedOn(ZonedDateTime.now());
		e.setDecidedBy(t.getDecidedBy());
		e.setIsValid(t.getIsValid());
		e.setRemarks(t.getRemarks());
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
}
