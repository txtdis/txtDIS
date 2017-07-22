package ph.txtdis.dto;

import static ph.txtdis.type.ModuleType.EX_TRUCK;
import static ph.txtdis.type.ModuleType.SALES_ORDER;

import lombok.Data;
import ph.txtdis.type.ModuleType;
import ph.txtdis.type.PartnerType;

@Data
public class Booking //
		implements Keyed<Long>, Typed {

	private Long id, bookingId;

	private String customer, deliveryRoute, location, route;

	@Override
	public ModuleType type() {
		if (customer != null && customer.startsWith(PartnerType.EX_TRUCK.toString()))
			return EX_TRUCK;
		return SALES_ORDER;
	}

	@Override
	public String toString() {
		return "\n        Booking No." + id + "b" + bookingId + ": " + customer //
				+ " from " + location + " of " + route + deliveryRoute();
	}

	private String deliveryRoute() {
		return deliveryRoute == null ? "" : " - " + deliveryRoute;
	}
}
