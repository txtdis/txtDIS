package ph.txtdis.mgdc.gsm.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.gsm.domain.CustomerEntity;
import ph.txtdis.repository.NameListRepository;
import ph.txtdis.repository.SpunRepository;
import ph.txtdis.type.LocationType;
import ph.txtdis.type.PartnerType;

@Repository("customerRepository")
public interface CustomerRepository extends NameListRepository<CustomerEntity>, SpunRepository<CustomerEntity, Long> {

	List<CustomerEntity> findByDeactivatedOnNullAndContactNameNotNullAndContactSurnameNotNull( //
			@Param("outlet") PartnerType t);

	List<CustomerEntity> findByDeactivatedOnNullAndType( //
			@Param("outlet") PartnerType t);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndBarangayTypeNot( //
			@Param("outlet") PartnerType t, //
			@Param("barangay") LocationType b);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndChannelVisitedTrueAndVisitFrequencyNotNullAndVisitScheduleNotNullOrderByVisitScheduleWeekNoAsc(
			@Param("outlet") PartnerType t);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndChannelVisitedTrueAndVisitFrequencyNull( //
			@Param("outlet") PartnerType t);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndChannelVisitedTrueAndVisitScheduleNull( //
			@Param("outlet") PartnerType t);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndCityTypeNot( //
			@Param("customer") PartnerType t, //
			@Param("city") LocationType c);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndContactNameNotNullAndContactSurnameNull( //
			@Param("outlet") PartnerType t);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndContactNameNotNullAndContactTitleNull( //
			@Param("outlet") PartnerType t);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndContactNameNotNullAndMobileNull( //
			@Param("outlet") PartnerType t);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndContactNameNullAndCreditDetailsNotNull( //
			@Param("outlet") PartnerType t);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndContactNameNullAndCustomerDiscountsNotNull( //
			@Param("outlet") PartnerType t);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndCreatedOnLessThan( //
			@Param("outlet") PartnerType t, //
			@Param("creation") ZonedDateTime zdt);

	CustomerEntity findByDeactivatedOnNullAndTypeAndId( //
			@Param("internal") PartnerType t, //
			@Param("id") Long id);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndProvinceTypeNot( //
			@Param("outlet") PartnerType t, //
			@Param("province") LocationType p);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeAndStreetNull( //
			@Param("outlet") PartnerType t);

	List<CustomerEntity> findByDeactivatedOnNullAndTypeOrderByNameAsc( //
			@Param("bank") PartnerType t);

	List<CustomerEntity> findByNameContainingOrderByNameAsc( //
			@Param("name") String n);
}
