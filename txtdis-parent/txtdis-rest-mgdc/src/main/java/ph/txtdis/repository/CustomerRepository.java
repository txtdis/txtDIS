package ph.txtdis.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.CustomerEntity;
import ph.txtdis.dto.PartnerType;
import ph.txtdis.type.LocationType;

@Repository("customerRepository")
public interface CustomerRepository extends NameListRepository<CustomerEntity>, SpunRepository<CustomerEntity, Long> {

	List<CustomerEntity> findByDeactivatedOnNullAndContactNameNotNullAndContactSurnameNotNull(
			@Param("outlet") PartnerType t);

	List<CustomerEntity> findByDeactivatedOnNullAndType(@Param("outlet") PartnerType t);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndBarangayTypeNot(@Param("outlet") PartnerType t,
			@Param("barangay") LocationType b);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndChannelVisitedTrueAndVisitFrequencyNotNullAndVisitScheduleNotNullOrderByVisitScheduleWeekNoAsc(
			@Param("outlet") PartnerType t);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndChannelVisitedTrueAndVisitFrequencyNull(
			@Param("outlet") PartnerType t);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndChannelVisitedTrueAndVisitScheduleNull(
			@Param("outlet") PartnerType t);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndCityTypeNot(@Param("customer") PartnerType t,
			@Param("city") LocationType c);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndContactNameNotNullAndContactSurnameNull(
			@Param("outlet") PartnerType t);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndContactNameNotNullAndContactTitleNull(
			@Param("outlet") PartnerType t);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndContactNameNotNullAndMobileNull(
			@Param("outlet") PartnerType t);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndContactNameNullAndCreditDetailsNotNull(
			@Param("outlet") PartnerType t);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndContactNameNullAndCustomerDiscountsNotNull(
			@Param("outlet") PartnerType t);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndProvinceTypeNot(@Param("outlet") PartnerType t,
			@Param("province") LocationType p);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndStreetNull(@Param("outlet") PartnerType t);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndUploadedOnNull(@Param("outlet") PartnerType t);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeOrderByNameAsc(@Param("bank") PartnerType t);

	List<CustomerEntity> findByNameContaining(@Param("name") String n);

	CustomerEntity findByVendorId(@Param("vendorId") Long id);
}
