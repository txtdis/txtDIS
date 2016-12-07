package ph.txtdis.service;

import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;

@Service("receivingReportService")
public class ReceivingReportServiceImpl extends AbstractReceivingReportService {

	@Override
	protected String getReferencePrompt(Billable b) {
		if (b.getCustomerName().startsWith("EX-TRUCK"))
			return "L/O";
		return "S/O";
	}
}
