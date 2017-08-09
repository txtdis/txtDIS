package ph.txtdis.mgdc.ccbpi.service;

import java.util.List;

import ph.txtdis.dto.PickListDetail;
import ph.txtdis.mgdc.service.LoadingService;
import ph.txtdis.service.AppendableDetailService;

public interface LoadReturnService //
	extends AppendableDetailService,
	LoadingService,
	UpToReturnableQtyReceivingService {

	@Override
	List<PickListDetail> getDetails();

	void setDetails(List<PickListDetail> items);

	String getTruck();

	void returnAllPickedItemsIfNoneOfItsOCSHasAnRR() throws Exception;

	void updateUponPickListIdValidation(Long id) throws Exception;
}
