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
import ph.txtdis.type.PartnerType;
import ph.txtdis.type.VisitFrequency;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(indexes = { @Index(columnList = "name"), @Index(columnList = "deactivated_on, type, name") })
public class Customer extends ModificationTracked<Long> {

	private static final long serialVersionUID = -878749889584633340L;

	private String street;

	@ManyToOne(optional = false)
	private Location barangay;

	@ManyToOne(optional = false)
	private Location city;

	@ManyToOne(optional = false)
	private Location province;

	@ManyToOne
	@JoinColumn(name = "primary_pricing")
	private PricingType primaryPricingType;

	@ManyToOne
	@JoinColumn(name = "alternate_pricing")
	private PricingType alternatePricingType;

	private PartnerType type;

	@ManyToOne
	private Channel channel;

	@Column(name = "visit_frequency")
	private VisitFrequency visitFrequency;

	@OrderBy("weekNo ASC")
	@OneToMany(cascade = ALL)
	@JoinColumn(name = "customer_id")
	private List<WeeklyVisit> visitSchedule;

	@JoinColumn(name = "customer_id")
	@OneToMany(cascade = ALL)
	private List<Routing> routeHistory;

	@Column(name = "contact_name")
	private String contactName;

	@Column(name = "contact_surname")
	private String contactSurname;

	@Column(name = "contact_title")
	private String contactTitle;

	private String mobile;

	@OneToMany(cascade = ALL)
	@JoinColumn(name = "customer_id")
	private List<CreditDetail> creditDetails;

	@OneToMany(cascade = ALL)
	@JoinColumn(name = "customer_id")
	@OrderBy("startDate DESC, familyLimit DESC, level ASC")
	private List<CustomerDiscount> customerDiscounts;

	@ManyToOne
	private Customer parent;
	
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
		return getId() == null ? null :getId().toString();
	}

	public String getLocation() {
		if (barangay == null)
			return "";
		return barangay + city();
	}

	private Route getRoute() {
		try {
			return getRouteHistory().stream().filter(p -> !p.getStartDate().isAfter(now()))
					.max((a, b) -> a.getStartDate().compareTo(b.getStartDate())).get().getRoute();
		} catch (Exception e) {
			return null;
		}
	}

	@JsonIgnore
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
}
