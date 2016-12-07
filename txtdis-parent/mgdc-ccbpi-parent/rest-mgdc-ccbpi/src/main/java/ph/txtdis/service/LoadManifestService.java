package ph.txtdis.service;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.domain.BomEntity;
import ph.txtdis.dto.Billable;

public interface LoadManifestService extends QtyPerItemService, SpunBillableService {

	Billable find(Long shipment);

	List<BomEntity> getBomList(LocalDate start, LocalDate end);
}
