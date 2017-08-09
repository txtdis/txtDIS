package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.mgdc.gsm.dto.Vat;
import ph.txtdis.service.ReportService;

import java.math.BigDecimal;

public interface VatService
	extends ReportService<Vat> {

	BigDecimal getVat(BigDecimal total);

	BigDecimal getVatable(BigDecimal total);

	BigDecimal getVatRate();
}