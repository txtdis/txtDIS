package ph.txtdis.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.Customer;
import ph.txtdis.type.LocationType;
import ph.txtdis.type.PartnerType;

@Repository("customerRepository")
public interface CustomerRepository extends NameListRepository<Customer>, SpunRepository<Customer, Long> {

	List<Customer> findByDeactivatedOnNullAndContactNameNotNullAndContactSurnameNotNull(
			@Param("customer") PartnerType t);

	List<Customer> findByDeactivatedOnNullAndTypeAndBarangayTypeNot(@Param("customer") PartnerType t,
			@Param("barangay") LocationType b);

	List<Customer> findByDeactivatedOnNullAndTypeAndChannelVisitedTrueAndVisitFrequencyNotNullAndVisitScheduleNotNullOrderByVisitScheduleWeekNoAsc(
			@Param("customer") PartnerType t);

	List<Customer> findByDeactivatedOnNullAndTypeAndChannelVisitedTrueAndVisitFrequencyNull(
			@Param("customer") PartnerType t);

	List<Customer> findByDeactivatedOnNullAndTypeAndChannelVisitedTrueAndVisitScheduleNull(
			@Param("customer") PartnerType t);

	List<Customer> findByDeactivatedOnNullAndTypeAndCityTypeNot(@Param("customer") PartnerType t,
			@Param("city") LocationType c);

	List<Customer> findByDeactivatedOnNullAndTypeAndContactNameNotNullAndContactSurnameNull(
			@Param("customer") PartnerType t);

	List<Customer> findByDeactivatedOnNullAndTypeAndContactNameNotNullAndContactTitleNull(
			@Param("customer") PartnerType t);

	List<Customer> findByDeactivatedOnNullAndTypeAndContactNameNotNullAndMobileNull(@Param("customer") PartnerType t);

	List<Customer> findByDeactivatedOnNullAndTypeAndContactNameNullAndCreditDetailsNotNull(
			@Param("customer") PartnerType t);

	List<Customer> findByDeactivatedOnNullAndTypeAndContactNameNullAndCustomerDiscountsNotNull(
			@Param("customer") PartnerType t);

	List<Customer> findByDeactivatedOnNullAndTypeAndProvinceTypeNot(@Param("customer") PartnerType t,
			@Param("province") LocationType p);

	List<Customer> findByDeactivatedOnNullAndTypeAndStreetNull(@Param("customer") PartnerType t);

	List<Customer> findByDeactivatedOnNullAndTypeAndUploadedOnNull(@Param("customer") PartnerType t);

	List<Customer> findByDeactivatedOnNullAndTypeOrderByNameAsc(@Param("bank") PartnerType t);

	List<Customer> findByNameContaining(@Param("name") String n);
}
