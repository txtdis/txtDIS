package ph.txtdis.mgdc.gsm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.txtdis.mgdc.domain.AbstractCustomerEntity;
import ph.txtdis.mgdc.domain.LocationEntity;
import ph.txtdis.mgdc.domain.RouteEntity;
import ph.txtdis.type.VisitFrequency;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.now;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static javax.persistence.CascadeType.ALL;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "customer", //
	indexes = { //
		@Index(columnList = "name"), //
		@Index(columnList = "type"), //
		@Index(columnList = "contact_name"), //
		@Index(columnList = "contact_surname"), //
		@Index(columnList = "contact_title"), //
		@Index(columnList = "mobile"), //
		@Index(columnList = "deactivated_on"), //
		@Index(columnList = "type"), //
		@Index(columnList = "channel_id"), //
		@Index(columnList = "visit_frequency"), //
		@Index(columnList = "barangay_id"), //
		@Index(columnList = "city_id"), //
		@Index(columnList = "province_id"), //
		@Index(columnList = "street") //
	})
public class CustomerEntity //
	extends AbstractCustomerEntity {

	private static final long serialVersionUID = 4005115994591616684L;

	private String street;

	@ManyToOne
	private ChannelEntity channel;

	@ManyToOne
	private LocationEntity barangay;

	@ManyToOne
	private LocationEntity city;

	@ManyToOne
	private LocationEntity province;

	@OrderBy("startDate DESC")
	@OneToMany(mappedBy = "customer", cascade = ALL)
	private List<RoutingEntity> routeHistory;

	@Column(name = "visit_frequency")
	private VisitFrequency visitFrequency;

	@OrderBy("weekNo ASC")
	@OneToMany(cascade = ALL)
	@JoinColumn(name = "customer_id")
	private List<WeeklyVisitEntity> visitSchedule;

	@Column(name = "contact_name")
	private String contactName;

	@Column(name = "contact_surname")
	private String contactSurname;

	@Column(name = "contact_title")
	private String contactTitle;

	private String mobile;

	@OneToMany(cascade = ALL)
	@JoinColumn(name = "customer_id")
	private List<CreditDetailEntity> creditDetails;

	@OneToMany(mappedBy = "customer", cascade = ALL)
	@OrderBy("startDate DESC")
	private List<CustomerDiscountEntity> customerDiscounts;

	@Transient
	private BigDecimal avgQtyPerMonth;

	@ManyToOne
	private CustomerEntity parent;

	@JsonIgnore
	public String getAddress() {
		return street() + barangay() + city() + province();
	}

	private String street() {
		return street == null ? "" : street;
	}

	private String barangay() {
		return barangay == null ? "" : (street != null ? ", " : "") + barangay;
	}

	private String city() {
		return city == null ? "" : (barangay != null || street != null ? ", " : "") + city;
	}

	private String province() {
		return province == null ? "" : (city != null || barangay != null || street != null ? ", " : "") + province;
	}

	public List<CreditDetailEntity> getCreditDetails() {
		return creditDetails == null ? emptyList() : creditDetails;
	}

	public String getLocation() {
		return barangay().replace(", ", "") + city();
	}

	public RouteEntity getRoute() {
		return getRoute(now());
	}

	public RouteEntity getRoute(LocalDate date) {
		return getRouteHistory().stream() //
			.filter(p -> !p.getStartDate().isAfter(date)) //
			.max(comparing(RoutingEntity::getStartDate)) //
			.orElse(new RoutingEntity()).getRoute();
	}

	public List<RoutingEntity> getRouteHistory() {
		return routeHistory == null ? emptyList() : routeHistory;
	}

	public String getSeller() {
		return getSeller(now());
	}

	public String getSeller(LocalDate d) {
		RouteEntity r = getRoute(d);
		return r == null ? null : r.getSeller(d);
	}

	@Override
	public String toString() {
		return getName();
	}
}
