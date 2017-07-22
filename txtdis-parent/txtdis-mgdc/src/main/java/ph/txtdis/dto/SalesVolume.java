package ph.txtdis.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import ph.txtdis.type.UomType;

@Data
public class SalesVolume //
		implements Keyed<Long>, Comparable<SalesVolume> {

	private boolean active;

	private Long id;

	private String billingNo, seller, delivery, channel, category, productLine, item, customer, street, barangay, city;

	private BigDecimal qty, vol;

	private LocalDate orderDate, customerStartDate;

	private UomType uom;

	@Override
	public int compareTo(SalesVolume v) {
		return getId().compareTo(v.getId());
	}

	public boolean getActive() {
		return active;
	}
}
