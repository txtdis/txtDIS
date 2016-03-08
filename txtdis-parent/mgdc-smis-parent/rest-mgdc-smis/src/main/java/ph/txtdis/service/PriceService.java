package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.repository.PriceRepository;

@Service("priceService")
public class PriceService implements DecisionDataUpdate {

	@Autowired
	private PriceRepository repository;

	public void updateDecisionData(String[] s) {
		updateDecisionData(repository, s);
	}
}
