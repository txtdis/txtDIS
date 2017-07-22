package ph.txtdis.mgdc.ccbpi.service.server;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.PickList;
import ph.txtdis.exception.FailedPrintingException;
import ph.txtdis.mgdc.ccbpi.domain.BomEntity;
import ph.txtdis.mgdc.ccbpi.domain.PickListEntity;
import ph.txtdis.service.SpunSavedKeyedService;

public interface PickListService extends FilteredListService, SpunSavedKeyedService<PickListEntity, PickList, Long> {

	List<PickList> findAllByDate(LocalDate d);

	@Override
	List<PickListEntity> list(String collector, LocalDate start, LocalDate end);

	PickList printPickList(Long id) throws FailedPrintingException;

	List<BomEntity> summaryOfQuantitiesPerItem(PickListEntity entity);

	PickListEntity toEntity(PickList p);

	PickList toModel(PickListEntity e);
}
