package ph.txtdis.mgdc.gsm.service;

import java.util.List;

import javafx.collections.ObservableList;
import ph.txtdis.dto.CreditDetail;
import ph.txtdis.mgdc.gsm.dto.Customer;

public interface CreditGivenCustomerService {

	String getContactName();

	String getContactSurname();

	String getContactTitle();

	List<CreditDetail> getCreditDetails();

	long getCreditTerm(Customer customer);

	String getMobile();

	void setContactName(String text);

	void setContactSurname(String text);

	void setContactTitle(String text);

	void setCreditDetails(ObservableList<CreditDetail> items);

	void validatePhoneNo(String ph) throws Exception;
}
