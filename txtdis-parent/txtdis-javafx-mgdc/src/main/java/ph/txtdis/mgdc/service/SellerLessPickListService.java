package ph.txtdis.mgdc.service;

import java.util.List;

public interface SellerLessPickListService
	extends PickListService {

	List<String> listCollectors();

	void setCollector(String name);
}
