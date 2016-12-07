package ph.txtdis.dto;

import static ph.txtdis.type.BillableType.EX_TRUCK;
import static ph.txtdis.type.BillableType.SALES_ORDER;

import lombok.Data;
import ph.txtdis.type.BillableType;

@Data
public class Booking implements Keyed<Long>, Typed {

	private Long id, bookingId;

	private String customer, location, route;

	@Override
	public BillableType type() {
		if (customer != null && customer.startsWith("EX-TRUCK"))
			return EX_TRUCK;
		return SALES_ORDER;
	}
}
