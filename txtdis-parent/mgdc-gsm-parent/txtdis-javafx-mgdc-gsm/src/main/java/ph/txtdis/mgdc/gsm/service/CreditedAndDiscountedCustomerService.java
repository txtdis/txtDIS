package ph.txtdis.mgdc.gsm.service;

import javafx.collections.ObservableList;
import ph.txtdis.dto.*;
import ph.txtdis.mgdc.gsm.dto.Channel;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.service.RouteAssignedCustomerService;
import ph.txtdis.type.PartnerType;
import ph.txtdis.type.VisitFrequency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface CreditedAndDiscountedCustomerService //
	extends CreditGivenCustomerService,
	CustomerService,
	RouteAssignedCustomerService {

	CreditDetail createCreditLineUponValidation(int termInDays,
	                                            int gracePeriodInDays,
	                                            BigDecimal creditLimit,
	                                            LocalDate start) throws Exception;

	Customer findNoContactDetails() throws Exception;

	Customer findNoDesignation() throws Exception;

	Customer findNoMobileNo() throws Exception;

	Customer findNoStreetAddress() throws Exception;

	Customer findNoSurname() throws Exception;

	Customer findNotCorrectBarangayAddress() throws Exception;

	Customer findNotCorrectCityAddress() throws Exception;

	Customer findNotCorrectProvincialAddress() throws Exception;

	Customer findNotTheSameWeeksOneAndFiveVisitSchedule() throws Exception;

	Customer findNoVisitSchedule() throws Exception;

	Location getBarangay();

	void setBarangay(Location barangay);

	Channel getChannel();

	void setChannel(Channel channel);

	Location getCity();

	void setCity(Location city);

	long getGracePeriod(Customer c);

	String getName();

	void setName(String name);

	Long getParentId();

	String getParentName();

	Location getProvince();

	void setProvince(Location province);

	List<Routing> getRouteHistory();

	void setRouteHistory(List<Routing> routings);

	String getStreet();

	void setStreet(String street);

	Customer getVendor() throws Exception;

	VisitFrequency getVisitFrequency();

	void setVisitFrequency(VisitFrequency freq);

	List<WeeklyVisit> getVisitSchedule(Channel c);

	List<Location> listBarangays(Location c);

	List<Customer> listByScheduledRouteAndGoodCreditStanding(Route r);

	List<Channel> listChannels();

	List<Location> listCities(Location p);

	List<String> listOutletNames();

	List<Location> listProvinces();

	List<PartnerType> listTypes();

	List<Channel> listVisitedChannels();

	void setNameUponValidation(String name) throws Exception;

	void setParentIfExists(Long id) throws Exception;

	void setRouteAsPickUpAndChannelAsWarehouseSales() throws Exception;

	void setVisitSchedule(List<WeeklyVisit> visits);

	void verifyUserIsAllowedToChangeSchedule(ObservableList<WeeklyVisit> old, ObservableList<WeeklyVisit> changed)
		throws Exception;
}
