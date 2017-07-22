package ph.txtdis.mgdc.ccbpi.service;

import java.util.List;

import ph.txtdis.info.Information;

public interface DeliveryListService //
		extends ShippedBillableService {

	void confirmNoDeliveryListOfSameDateExists() throws Exception;

	String getRoutePrompt();

	void initializeMap();

	List<String> listRouteNames();

	void mapIndexToDDL(int columnIndex, String text);

	void saveExtractedDDLs() throws Information, Exception;

	void setItemUponValidation(int vendorId) throws Exception;

	void setRouteItemQtyUponValidation(int columnIdx, double qty) throws Exception;

	void updateUponRouteValidation(String route) throws Exception;

	void validateNameIsOfClient(String text) throws Exception;

	void validateNamesAreOfRoutes() throws Exception;
}
