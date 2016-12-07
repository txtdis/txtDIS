package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.BillableEntity;
import ph.txtdis.dto.Billable;
import ph.txtdis.exception.FailedPrintingException;
import ph.txtdis.printer.ReturnedMaterialPrinter;

@Service("billableService")
public class BillingServiceImpl extends AbstractBillableService {

	@Autowired
	private ReturnedMaterialPrinter returnedMaterialPrinter;

	public Billable printPickList(Long id) throws FailedPrintingException {
		BillableEntity b = repository.findOne(id);
		print(b);
		return toDTO(b);
	}

	private void print(BillableEntity b) throws FailedPrintingException {
		try {
			returnedMaterialPrinter.print(b);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FailedPrintingException(e.getMessage());
		}
	}
}