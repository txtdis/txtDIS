package ph.txtdis.mgdc.gsm.service.server;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.domain.RemittanceDetailEntity;

@Service("postDatedChequeUpdateService")
public class PostDatedChequeUpdateServiceImpl //
		implements PostDatedChequeUpdateService {

	@Autowired
	private GsmRemittanceService service;

	@Override
	public void setFullyPaidForMaturedPostDatedChecks() {
		List<RemittanceDetailEntity> l = service.findFullyPaidEntitiesForMaturedPostDatedChecks();
		service.saveDetails(setFullyPaid(l));
	}

	private List<RemittanceDetailEntity> setFullyPaid(List<RemittanceDetailEntity> l) {
		return l.stream().map(d -> setFullyPaid(d)).collect(Collectors.toList());
	}

	private RemittanceDetailEntity setFullyPaid(RemittanceDetailEntity d) {
		BillableEntity b = d.getBilling();
		if (b != null)
			b.setFullyPaid(true);
		d.setBilling(b);
		return d;
	}
}
