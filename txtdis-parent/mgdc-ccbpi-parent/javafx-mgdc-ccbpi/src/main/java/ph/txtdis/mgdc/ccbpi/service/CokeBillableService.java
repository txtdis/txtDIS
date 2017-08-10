package ph.txtdis.mgdc.ccbpi.service;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.service.SubheadedTotaledService;

import java.time.LocalDate;
import java.util.List;

public interface CokeBillableService //
	extends BillableService,
	UpToReturnableQtyReceivingService,
	SubheadedTotaledService<BillableDetail> {

	String getReferenceOrderNo();

	default boolean isADeliveryListOrAnOrderConfirmationOrALoadManifest() {
		return isADeliveryListOrAnOrderConfirmation() || isALoadManifest();
	}

	default boolean isADeliveryListOrAnOrderConfirmation() {
		return isADeliveryList() || isAnOrderConfirmation();
	}

	boolean isALoadManifest();

	boolean isADeliveryList();

	boolean isAnOrderConfirmation();

	List<String> listRoutes();

	List<String> listTypes();

	void setDueDate(LocalDate due);

	void setRoute(String route);

	void setType(String type);

	void updateUponCustomerVendorIdValidation(Long id) throws Exception;

	void updateUponReferenceOrderNoValidation(String orderNo) throws Exception;
}
