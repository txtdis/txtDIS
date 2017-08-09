package ph.txtdis.mgdc.ccbpi.service;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.PickList;
import ph.txtdis.dto.PickListDetail;
import ph.txtdis.service.AppendableDetailService;
import ph.txtdis.service.RestClientService;
import ph.txtdis.service.ResettableService;
import ph.txtdis.service.RestClientService;
import ph.txtdis.service
	.SpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService;

public interface DeliveryReturnService //
	extends UpToReturnableQtyReceivingService,
	AppendableDetailService,
	ResettableService,
	SpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService<Long> {

	@Override
	List<PickListDetail> getDetails();

	LocalDate getPickDate();

	void updateUponPickListIdValidation(Long id) throws Exception;
}
