package ph.txtdis.service;

import java.math.BigDecimal;

import ph.txtdis.dto.Vat;

public interface VatService extends ReportService<Vat> {

	BigDecimal getVat(BigDecimal total);

	BigDecimal getVatable(BigDecimal total);

	BigDecimal getVatRate();
}