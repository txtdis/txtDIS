package ph.txtdis.mgdc.ccbpi.service;

import ph.txtdis.dto.PickListDetail;
import ph.txtdis.service.AppendableDetailService;
import ph.txtdis.service.ResettableService;
import ph.txtdis.service
	.SpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService;

import java.time.LocalDate;
import java.util.List;

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
