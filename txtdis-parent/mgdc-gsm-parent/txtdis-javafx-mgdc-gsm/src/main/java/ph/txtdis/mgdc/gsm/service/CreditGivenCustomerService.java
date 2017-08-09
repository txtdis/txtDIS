package ph.txtdis.mgdc.gsm.service;

import javafx.collections.ObservableList;
import ph.txtdis.dto.CreditDetail;
import ph.txtdis.mgdc.gsm.dto.Customer;

import java.util.List;

public interface CreditGivenCustomerService {

	String getContactName();

	void setContactName(String text);

	String getContactSurname();

	void setContactSurname(String text);

	String getContactTitle();

	void setContactTitle(String text);

	List<CreditDetail> getCreditDetails();

	void setCreditDetails(ObservableList<CreditDetail> items);

	long getCreditTerm(Customer customer);

	String getMobile();

	void validatePhoneNo(String ph) throws Exception;
}
