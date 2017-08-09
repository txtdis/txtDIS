package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.PickList;
import ph.txtdis.exception.FailedPrintingException;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.domain.BomEntity;
import ph.txtdis.mgdc.gsm.domain.PickListEntity;
import ph.txtdis.service.SpunSavedKeyedService;

import java.time.LocalDate;
import java.util.List;

public interface PickListService
	extends SpunSavedKeyedService<PickListEntity, PickList, Long> {

	List<PickList> findAll();

	List<PickList> findAllByDate(LocalDate d);

	List<PickList> findAllWithReturns();

	void postToEdms(BillableEntity e);

	PickList printPickList(Long id) throws FailedPrintingException;

	PickList saveToEdms(PickList b) throws Exception;

	List<BomEntity> summaryOfQuantitiesPerItem(PickListEntity entity);

	PickListEntity toEntity(PickList p);

	PickList toModel(PickListEntity e);
}