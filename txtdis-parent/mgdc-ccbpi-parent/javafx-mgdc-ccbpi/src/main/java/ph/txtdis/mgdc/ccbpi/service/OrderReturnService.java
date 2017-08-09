package ph.txtdis.mgdc.ccbpi.service;

import java.util.List;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.service.TotaledService;
import ph.txtdis.type.OrderReturnType;

public interface OrderReturnService //
	extends BillableService,
	UpToReturnableQtyReceivingService,
	TotaledService<BillableDetail> {

	String getCollectorName();

	String getOrderConfirmationPrompt();

	List<OrderReturnType> listReasons();

	void setOrderUponValidation(String ocsId) throws Exception;

	void updateUponReasonValidation(OrderReturnType reason) throws Exception;
}
