package ph.txtdis.dto;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.VisitFrequency;

@Data
@EqualsAndHashCode(callSuper = true)
public class Customer extends AbstractModificationTracked<Long> implements SalesforceEntity {

	private Channel channel;

	private Customer parent;

	private List<CreditDetail> creditDetails;

	private List<CustomerDiscount> customerDiscounts;

	private List<CustomerVolumeDiscount> volumeDiscounts;

	private List<CustomerVolumePromo> volumePromos;

	private List<Routing> routeHistory;

	private List<WeeklyVisit> visitSchedule;

	private Location barangay, city, province;

	private Long vendorId;

	private PartnerType type;

	private PricingType alternatePricingType, primaryPricingType;

	private String contactName, contactSurname, contactTitle, mobile, name, street, uploadedBy;

	private VisitFrequency visitFrequency;

	private ZonedDateTime uploadedOn;

	public String getAddress() {
		return street() + barangay() + city() + province();
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
		if (city == null)
			return "";
		return (!barangay().isEmpty() || !street().isEmpty() ? ", " : "") + city;
	}

	private String province() {
		if (province == null)
			return "";
		return (!city().isEmpty() || !barangay().isEmpty() || !street().isEmpty() ? ", " : "") + province;
	}

	public boolean areDeliveriesBooked(LocalDate d) {
		return getRoute(d) == null ? true : getRoute().getName().startsWith("PRE-SELL");
	}

	public boolean areDeliveriesPickedUp(LocalDate d) {
		return getRoute(d) == null ? true : getRoute().getName().equalsIgnoreCase("PICK-UP");
	}

	public CreditDetail getCredit(LocalDate date) {
		try {
			return getCreditDetails().stream().filter(p -> !p.getStartDate().isAfter(date))
					.max((a, b) -> a.getStartDate().compareTo(b.getStartDate())).get();
		} catch (Exception e) {
			return null;
		}
	}

	public Integer getGracePeriod() {
		try {
			int grace = getCredit(LocalDate.now()).getGracePeriodInDays();
			return grace == 0 ? null : grace;
		} catch (Exception e) {
			return null;
		}
	}

	public String getDiscountType() {
		try {
			if (getCustomerDiscounts() == null || getCustomerDiscounts().isEmpty())
				return null;
			if (getChannel().getName().startsWith("RTW"))
				return "RTW";
			return "STD";
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getIdNo() {
		return getId() == null ? null : getId().toString();
	}

	public Route getRoute() {
		return getRoute(LocalDate.now());
	}

	public Route getRoute(LocalDate date) {
		try {
			return getRouteHistory().stream().filter(p -> !p.getStartDate().isAfter(date))
					.max((a, b) -> a.getStartDate().compareTo(b.getStartDate())).get().getRoute();
		} catch (Exception e) {
			return null;
		}
	}

	public long getCreditTerm() {
		try {
			return getCredit(LocalDate.now()).getTermInDays();
		} catch (Exception e) {
			return 0;
		}
	}

	public String getSeller() {
		return getSeller(LocalDate.now());
	}

	public String getSeller(LocalDate date) {
		try {
			return getRoute(date).getSeller(date);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return name;
	}
}
