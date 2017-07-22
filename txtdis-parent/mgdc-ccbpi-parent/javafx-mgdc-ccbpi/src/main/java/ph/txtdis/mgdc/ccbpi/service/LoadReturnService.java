package ph.txtdis.mgdc.ccbpi.service;

import java.util.List;

import ph.txtdis.dto.PickListDetail;
import ph.txtdis.mgdc.service.LoadingService;
import ph.txtdis.service.AppendableDetailService;

public interface LoadReturnService //
		extends AppendableDetailService, LoadingService, UpToReturnableQtyReceivingService {

	@Override
	List<PickListDetail> getDetails();

	String getTruck();

	void returnAllPickedItemsIfNoneOfItsOCSHasAnRR() throws Exception;

	void setDetails(List<PickListDetail> items);

	void updateUponPickListIdValidation(Long id) throws Exception;
}
