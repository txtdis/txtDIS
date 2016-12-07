package ph.txtdis.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Channel;
import ph.txtdis.dto.CreditDetail;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.Location;
import ph.txtdis.dto.PartnerType;
import ph.txtdis.dto.Route;
import ph.txtdis.dto.Routing;
import ph.txtdis.dto.WeeklyVisit;
import ph.txtdis.type.BillingType;
import ph.txtdis.type.VisitFrequency;

public interface CustomerService extends ByNameSearchable<Customer>, DateValidated, DecisionNeeded, Excel<Customer>,
		ItemFamilyLimited, Reset, Serviced<Long>, ServiceDeactivated<Long> {

	CreditDetail createCreditLineUponValidation(int termInDays, int gracePeriodInDays, BigDecimal creditLimit,
			LocalDate startDate) throws Exception;

	Routing createRouteAssignmentUponValidation(Route route, LocalDate startDate) throws Exception;

	Customer findByName(String name) throws Exception;

	Customer findByVendorId(Long id) throws Exception;

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

	BillingType getBillingType(Billable b) throws Exception;

	Channel getChannel();

	Location getCity();

	String getName();

	Long getParentId();

	String getParentName();

	Location getProvince();

	List<Routing> getRouteHistory();

	String getStreet();

	Customer getVendor() throws Exception;

	VisitFrequency getVisitFrequency();

	List<WeeklyVisit> getVisitSchedule(Channel c);

	boolean isDeliveryScheduledOnThisDate(Customer c, LocalDate d);

	boolean isOffSite();

	List<String> listBankNames();

	List<Customer> listBanks();

	List<Location> listBarangays(Location c);

	List<Customer> listByScheduledRouteAndGoodCreditStanding(Route selectedRoute);

	List<Channel> listChannels();

	List<Location> listCities(Location p);

	List<String> listExTrucks();

	List<Location> listProvinces();

	List<PartnerType> listTypes();

	List<Channel> listVisitedChannels();

	boolean noChangesNeedingApproval(String text);

	void setBarangay(Location barangay);

	void setChannel(Channel channel);

	void setCity(Location city);

	void setName(String name);

	void setNameIfUnique(String name) throws Exception;

	void setParentIfExists(Long id) throws Exception;

	void setProvince(Location province);

	void setRouteHistory(List<Routing> routings);

	void setStreet(String street);

	void setType(PartnerType type);

	void setVisitFrequency(VisitFrequency freq);

	void setVisitSchedule(List<WeeklyVisit> visits);

	void validatePhoneNo(String ph) throws Exception;
}
