package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.repository.CreditDetailRepository;

@Service("creditDetailService")
public class CreditDetailService implements DecisionDataUpdate {

	@Autowired
	private CreditDetailRepository repository;

	public void updateDecisionData(String[] s) {
		updateDecisionData(repository, s);
	}
}
