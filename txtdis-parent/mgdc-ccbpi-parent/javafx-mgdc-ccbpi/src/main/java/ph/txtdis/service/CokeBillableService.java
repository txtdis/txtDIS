package ph.txtdis.service;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.BillableDetail;

public interface CokeBillableService
		extends BillableService, CokeReceivingService, TotaledTableService<BillableDetail> {

	String getReferenceOrderNo();

	boolean isADeliveryList();

	default boolean isADeliveryListOrAnOrderConfirmation() {
		return isADeliveryList() || isAnOrderConfirmation();
	}

	default boolean isADeliveryListOrAnOrderConfirmationOrALoadManifest() {
		return isADeliveryListOrAnOrderConfirmation() || isALoadManifest();
	}

	boolean isALoadManifest();

	boolean isAnOrderConfirmation();

	List<String> listRoutes();

	List<String> listTypes();

	void setDueDate(LocalDate due);

	void setRoute(String route);

	void setType(String type);

	void updateUponCustomerVendorIdValidation(Long id) throws Exception;

	void updateUponReferenceOrderNoValidation(String orderNo) throws Exception;
}
