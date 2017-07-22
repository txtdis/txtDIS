package ph.txtdis.mgdc.gsm.dto;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.maxBy;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.AbstractModificationTracked;
import ph.txtdis.dto.CreditDetail;
import ph.txtdis.dto.Location;
import ph.txtdis.dto.PricingType;
import ph.txtdis.dto.Route;
import ph.txtdis.dto.Routing;
import ph.txtdis.dto.WeeklyVisit;
import ph.txtdis.type.PartnerType;
import ph.txtdis.type.VisitFrequency;

@Data
@EqualsAndHashCode(callSuper = true)
public class Customer //
		extends AbstractModificationTracked<Long> {

	private Channel channel;

	private Customer parent;

	private List<CreditDetail> creditDetails;

	private List<CustomerDiscount> discounts;

	private List<Routing> routeHistory;

	private List<WeeklyVisit> visitSchedule;

	private Location barangay, city, province;

	private PartnerType type;

	private PricingType primaryPricingType;

	private String contactName, contactSurname, contactTitle, mobile, name, street;

	private VisitFrequency visitFrequency;

	public boolean getActive() {
		return getDeactivatedOn() == null;
	}

	public String getAddress() {
		return street() + barangay() + city();
	}

	private String street() {
		return street == null ? "" : street;
	}

	private String barangay() {
		if (barangay == null)
			return "";
		return (!street().isEmpty() ? ", " : "") + barangay;
	}

	private String city() {
		return city == null ? "" : (!barangay().isEmpty() || !street().isEmpty() ? ", " : "") + city;
	}

	public List<CreditDetail> getCreditDetails() {
		return creditDetails == null ? emptyList() : creditDetails;
	}

	public Integer getCreditTermInDays() {
		return getCreditDetails().isEmpty() ? null //
				: getCreditDetails().stream() //
						.max(comparing(CreditDetail::getStartDate)) //
						.get().getTermInDays();
	}

	public List<CustomerDiscount> getDiscounts() {
		return discounts == null ? emptyList() : discounts;
	}

	public Boolean getDiscounted() {
		return getDiscounts().stream() //
				.anyMatch(d -> d.getIsValid() != null && d.getIsValid() == true);
	}

	public Integer getGracePeriodInDays() {
		return getCreditDetails().isEmpty() ? null //
				: getCreditDetails().stream() //
						.filter(c -> c.getIsValid() != null && c.getIsValid() == true && c.getGracePeriodInDays() > 0)
						.max(comparing(CreditDetail::getStartDate)) //
						.orElse(new CreditDetail()).getGracePeriodInDays();
	}

	public Route getRoute() {
		return getRoute(LocalDate.now());
	}

	public Route getRoute(LocalDate date) {
		return getRouteHistory().stream() //
				.filter(p -> !p.getStartDate().isAfter(date)) //
				.collect(maxBy(comparing(Routing::getStartDate)))//
				.orElse(new Routing()).getRoute();
	}

	public List<Routing> getRouteHistory() {
		return routeHistory == null ? emptyList() : routeHistory;
	}

	public String getSeller() {
		return getSeller(LocalDate.now());
	}

	public String getSeller(LocalDate date) {
		Route route = getRoute(date);
		return route == null ? null : route.getSeller(date);
	}

	public String getVisitDay() {
		List<WeeklyVisit> l = getVisitSchedule();
		if (l != null && !l.isEmpty())
			for (WeeklyVisit v : l) {
				String day = day(v);
				if (day != null)
					return day;
			}
		return null;
	}

	private String day(WeeklyVisit v) {
		if (v.isSun())
			return "SUN";
		if (v.isMon())
			return "MON";
		if (v.isTue())
			return "TUE";
		if (v.isWed())
			return "WED";
		if (v.isThu())
			return "THU";
		if (v.isFri())
			return "FRI";
		if (v.isSat())
			return "SAT";
		return null;
	}

	@Override
	public String toString() {
		return name;
	}
}
