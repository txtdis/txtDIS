package ph.txtdis.mgdc.ccbpi.service.server;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;

public interface UnpickedOrderService //
	extends FilteredListService {

	@Override
	List<BillableEntity> list(String collector, LocalDate start, LocalDate end);
}
