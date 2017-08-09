package ph.txtdis.mgdc.ccbpi.service.server;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;
import ph.txtdis.mgdc.ccbpi.domain.BomEntity;
import ph.txtdis.mgdc.service.server.SpunSavedReferencedKeyedService;

public interface LoadManifestService //
	extends QtyPerItemService,
	SpunSavedReferencedKeyedService<BillableEntity, Billable, Long> {

	Billable find(Long shipment);

	List<BomEntity> getBomList(LocalDate start, LocalDate end);

	List<BillableEntity> list(LocalDate start, LocalDate end);
}
