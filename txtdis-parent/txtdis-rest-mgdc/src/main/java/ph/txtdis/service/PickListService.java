package ph.txtdis.service;

import java.sql.Date;
import java.util.List;

import ph.txtdis.domain.BomEntity;
import ph.txtdis.domain.PickListEntity;
import ph.txtdis.dto.PickList;
import ph.txtdis.exception.FailedPrintingException;

public interface PickListService extends SpunService<PickList, Long> {

	List<PickList> findByDate(Date d);

	PickListEntity findEntity(Long pickListId);

	PickList printPickList(Long id) throws FailedPrintingException;

	List<BomEntity> summaryOfQuantitiesPerItem(PickListEntity entity);

	PickList toDTO(PickListEntity e);

	PickListEntity toEntity(PickList p);
}