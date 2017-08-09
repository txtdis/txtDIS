package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.mgdc.gsm.dto.Vat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface VatService {

	BigDecimal computeVat(BigDecimal v);

	Vat getVat();

	List<Vat> list(LocalDate start, LocalDate end);
}
