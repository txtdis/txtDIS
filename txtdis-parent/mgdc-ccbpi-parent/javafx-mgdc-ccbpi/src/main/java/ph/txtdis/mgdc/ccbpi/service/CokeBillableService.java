package ph.txtdis.mgdc.ccbpi.service;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.service.SubheadedTotaledService;

public interface CokeBillableService //
		extends BillableService, UpToReturnableQtyReceivingService, SubheadedTotaledService<BillableDetail> {

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
