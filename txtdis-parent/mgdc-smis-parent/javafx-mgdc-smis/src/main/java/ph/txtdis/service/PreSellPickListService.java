package ph.txtdis.service;

import java.util.List;

public interface PreSellPickListService {

	List<String> listAsstHelpers();

	void setAsstHelperUponValidation(String value) throws Exception;
}
