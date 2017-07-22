package ph.txtdis.mgdc.ccbpi.service.server;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.mgdc.ccbpi.domain.DetailedEntity;

public interface FilteredListService {

	List<? extends DetailedEntity> list(String filter, LocalDate start, LocalDate end);
}
