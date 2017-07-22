package ph.txtdis.mgdc.ccbpi.dto;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.maxBy;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.AbstractModificationTracked;
import ph.txtdis.dto.Location;
import ph.txtdis.dto.PricingType;
import ph.txtdis.dto.Route;
import ph.txtdis.dto.Routing;
import ph.txtdis.type.PartnerType;

@Data
@EqualsAndHashCode(callSuper = true)
public class Customer //
		extends AbstractModificationTracked<Long> {

	private Channel channel;

	private Customer parent;

	private List<Routing> routeHistory;

	private Location barangay, city, province;

	private Long vendorId;

	private PartnerType type;

	private PricingType alternatePricingType, primaryPricingType;

	private String contactName, contactSurname, contactTitle, mobile, name, street;

	private List<CustomerDiscount> discounts;

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

	@Override
	public String toString() {
		return name;
	}
}
