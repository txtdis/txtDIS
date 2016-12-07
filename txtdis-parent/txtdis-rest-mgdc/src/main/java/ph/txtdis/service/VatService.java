package ph.txtdis.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.Vat;

public interface VatService {

	BigDecimal computeVat(BigDecimal v);

	Vat getVat();

	List<Vat> list(LocalDate start, LocalDate end);
}
