package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.repository.CustomerDiscountRepository;

@Service("customerDiscountService")
public class CustomerDiscountService implements DecisionDataUpdate {

	@Autowired
	private CustomerDiscountRepository repository;

	public void updateDecisionData(String[] s) {
		updateDecisionData(repository, s);
	}
}
