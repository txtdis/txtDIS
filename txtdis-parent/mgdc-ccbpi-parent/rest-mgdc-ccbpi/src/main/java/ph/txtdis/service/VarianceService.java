package ph.txtdis.service;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.SalesItemVariance;

public interface VarianceService {

	List<SalesItemVariance> listByDate(LocalDate start, LocalDate end);
}
