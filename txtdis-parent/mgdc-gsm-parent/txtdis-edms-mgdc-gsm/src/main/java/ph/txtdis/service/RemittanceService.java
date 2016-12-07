package ph.txtdis.service;

import java.math.BigDecimal;
import java.util.List;

import ph.txtdis.domain.EdmsInvoice;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Remittance;

public interface RemittanceService {

	List<Remittance> list();

	BigDecimal getUnpaidAmount(EdmsInvoice i);

	Boolean isFullyPaid(Billable b);
}