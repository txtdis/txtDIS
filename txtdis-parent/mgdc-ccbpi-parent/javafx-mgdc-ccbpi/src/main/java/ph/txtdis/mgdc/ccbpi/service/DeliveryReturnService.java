package ph.txtdis.mgdc.ccbpi.service;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.PickList;
import ph.txtdis.dto.PickListDetail;
import ph.txtdis.service.AppendableDetailService;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.service.ResettableService;
import ph.txtdis.service.SavingService;
import ph.txtdis.service.SpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService;

public interface DeliveryReturnService //
		extends UpToReturnableQtyReceivingService, AppendableDetailService, ResettableService,
		SpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService<Long> {

	@Override
	List<PickListDetail> getDetails();

	LocalDate getPickDate();

	@Override
	@SuppressWarnings("unchecked")
	ReadOnlyService<PickList> getReadOnlyService();

	@Override
	@SuppressWarnings("unchecked")
	SavingService<PickList> getSavingService();

	void updateUponPickListIdValidation(Long id) throws Exception;
}
