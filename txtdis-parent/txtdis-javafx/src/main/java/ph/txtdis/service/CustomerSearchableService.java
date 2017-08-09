package ph.txtdis.service;

public interface CustomerSearchableService
	extends CustomerIdAndNameService {

	void searchForCustomer(String name) throws Exception;
}
