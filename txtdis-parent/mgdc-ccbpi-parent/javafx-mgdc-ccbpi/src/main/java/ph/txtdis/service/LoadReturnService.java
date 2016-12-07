package ph.txtdis.service;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.PickList;
import ph.txtdis.dto.PickListDetail;

public interface LoadReturnService extends CokeReceivingService, Detailed, Reset, Serviced<Long> {

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
