package ph.txtdis.service;

import ph.txtdis.domain.EdmsInvoice;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Remittance;

import java.math.BigDecimal;
import java.util.List;

public interface EdmsRemittanceService {

	List<Remittance> list();

	BigDecimal getUnpaidAmount(EdmsInvoice i);

	Boolean isFullyPaid(Billable b);
}