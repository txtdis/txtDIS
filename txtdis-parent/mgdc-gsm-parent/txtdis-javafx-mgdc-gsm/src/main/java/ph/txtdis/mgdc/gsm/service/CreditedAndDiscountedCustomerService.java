package ph.txtdis.mgdc.gsm.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javafx.collections.ObservableList;
import ph.txtdis.dto.CreditDetail;
import ph.txtdis.dto.Location;
import ph.txtdis.dto.Route;
import ph.txtdis.dto.Routing;
import ph.txtdis.dto.WeeklyVisit;
import ph.txtdis.mgdc.gsm.dto.Channel;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.service.RouteAssignedCustomerService;
import ph.txtdis.type.PartnerType;
import ph.txtdis.type.VisitFrequency;

public interface CreditedAndDiscountedCustomerService //
		extends CreditGivenCustomerService, CustomerService, RouteAssignedCustomerService {

	CreditDetail createCreditLineUponValidation(int termInDays, int gracePeriodInDays, BigDecimal creditLimit, LocalDate start) throws Exception;

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

	Channel getChannel();

	Location getCity();

	long getGracePeriod(Customer c);

	String getName();

	Long getParentId();

	String getParentName();

	Location getProvince();

	List<Routing> getRouteHistory();

	String getStreet();

	Customer getVendor() throws Exception;

	VisitFrequency getVisitFrequency();

	List<WeeklyVisit> getVisitSchedule(Channel c);

	List<Location> listBarangays(Location c);

	List<Customer> listByScheduledRouteAndGoodCreditStanding(Route r);

	List<Channel> listChannels();

	List<Location> listCities(Location p);

	List<String> listOutletNames();

	List<Location> listProvinces();

	List<PartnerType> listTypes();

	List<Channel> listVisitedChannels();

	void setBarangay(Location barangay);

	void setChannel(Channel channel);

	void setCity(Location city);

	void setName(String name);

	void setNameUponValidation(String name) throws Exception;

	void setParentIfExists(Long id) throws Exception;

	void setProvince(Location province);

	void setRouteAsPickUpAndChannelAsWarehouseSales() throws Exception;

	void setRouteHistory(List<Routing> routings);

	void setStreet(String street);

	void setVisitFrequency(VisitFrequency freq);

	void setVisitSchedule(List<WeeklyVisit> visits);

	void verifyUserIsAllowedToChangeSchedule(ObservableList<WeeklyVisit> old, ObservableList<WeeklyVisit> changed) throws Exception;
}
