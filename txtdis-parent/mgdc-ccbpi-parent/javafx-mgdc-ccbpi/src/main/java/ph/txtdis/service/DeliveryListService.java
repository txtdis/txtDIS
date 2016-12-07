package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.BillableDetail;

public interface DeliveryListService extends BillableService, TotaledTableService<BillableDetail> {

	Long getDeliveryListId();

	String getDeliveryListPrompt();

	String getRoutePrompt();

	List<String> listRoutes();

	void setRoute(String name);

	void updateUponReferenceIdValidation(Long id) throws Exception;
}
