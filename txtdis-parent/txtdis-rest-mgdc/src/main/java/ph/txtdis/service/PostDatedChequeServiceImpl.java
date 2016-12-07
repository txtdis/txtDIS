package ph.txtdis.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.RemittanceDetailEntity;

@Service("postDatedChequeService")
public class PostDatedChequeServiceImpl implements PostDatedChequeService {

	@Autowired
	private RemittanceService service;

	@Override
	public void setFullyPaidForMaturedPostDatedChecks() {
		List<RemittanceDetailEntity> l = service.listFullyPaidForMaturedPostDatedChecks();
		l.forEach(d -> d.getBilling().setFullyPaid(true));
		service.saveDetails(l);
	}
}
