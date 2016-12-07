package ph.txtdis.domain;

import static java.time.LocalDate.now;
import static javax.persistence.CascadeType.ALL;

import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.PartnerType;
import ph.txtdis.type.VisitFrequency;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "customer", //
		indexes = { //
				@Index(columnList = "name"), //
				@Index(columnList = "type"), //
				@Index(columnList = "deactivated_on, contact_name, contact_surname"), //
				@Index(columnList = "deactivated_on, type, contact_name, contact_surname"), //
				@Index(columnList = "deactivated_on, type, contact_name, contact_title"), //
				@Index(columnList = "deactivated_on, type, contact_name, mobile"), //
				@Index(columnList = "deactivated_on, type, channel_id, visit_frequency"), //
				@Index(columnList = "deactivated_on, type, barangay_id"), //
				@Index(columnList = "deactivated_on, type, city_id"), //
				@Index(columnList = "deactivated_on, type, province_id"), //
				@Index(columnList = "deactivated_on, type, street"), //
				@Index(columnList = "deactivated_on, type, uploaded_on"), //
				@Index(columnList = "deactivated_on, type, name"), //
				@Index(columnList = "vendor_id") //
		})
public class CustomerEntity extends AbstractModifiedEntity<Long> {

	private static final long serialVersionUID = -878749889584633340L;

	private String street;

	@ManyToOne
	private LocationEntity barangay;

	@ManyToOne
	private LocationEntity city;

	@ManyToOne
	private LocationEntity province;

	@ManyToOne
	@JoinColumn(name = "primary_pricing")
	private PricingTypeEntity primaryPricingType;

	@ManyToOne
	@JoinColumn(name = "alternate_pricing")
	private PricingTypeEntity alternatePricingType;

	private PartnerType type;

	@ManyToOne
	private ChannelEntity channel;

	@Column(name = "visit_frequency")
	private VisitFrequency visitFrequency;

	@OrderBy("weekNo ASC")
	@OneToMany(cascade = ALL)
	@JoinColumn(name = "customer_id")
	private List<WeeklyVisitEntity> visitSchedule;

	@JoinColumn(name = "customer_id")
	@OneToMany(cascade = ALL)
	private List<RoutingEntity> routeHistory;

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

	@OneToMany(cascade = ALL)
	@JoinColumn(name = "customer_id")
	@OrderBy("startDate DESC, familyLimit DESC, level ASC")
	private List<CustomerDiscountEntity> customerDiscounts;

	@OneToMany(cascade = ALL)
	@JoinColumn(name = "customer_id")
	@OrderBy("startDate DESC, item ASC")
	private List<CustomerVolumeDiscountEntity> volumeDiscounts;

	@OneToMany(cascade = ALL)
	@JoinColumn(name = "customer_id")
	@OrderBy("startDate DESC, item ASC")
	private List<CustomerVolumePromoEntity> volumePromos;

	@ManyToOne
	private CustomerEntity parent;

	@Column(name = "vendor_id")
	private Long vendorId;

	@Column(name = "uploaded_by")
	private String uploadedBy;

	@Column(name = "uploaded_on")
	private ZonedDateTime uploadedOn;

	private String barangay() {
		if (barangay == null)
			return "";
		return (street != null ? ", " : "") + barangay;
	}

	private String city() {
		if (city == null)
			return "";
		return (barangay != null || street != null ? ", " : "") + city;
	}

	@JsonIgnore
	public String getAddress() {
		return street() + barangay() + city() + province();
	}

	public String getIdNo() {
		return getId() == null ? null : getId().toString();
	}

	public String getLocation() {
		return barangay().replace(", ", "") + city();
	}

	public RouteEntity getRoute() {
		try {
			return getRouteHistory().stream().filter(p -> !p.getStartDate().isAfter(now()))
					.max((a, b) -> a.getStartDate().compareTo(b.getStartDate())).get().getRoute();
		} catch (Exception e) {
			return null;
		}
	}

	public String getSeller() {
		try {
			return getRoute().getSeller(now());
		} catch (Exception e) {
			return null;
		}
	}

	private String province() {
		if (province == null)
			return "";
		return (city != null || barangay != null || street != null ? ", " : "") + province;
	}

	private String street() {
		return street == null ? "" : street;
	}

	@Override
	public String toString() {
		return "[" + name + ": " + getAddress() + "]";
	}
}
