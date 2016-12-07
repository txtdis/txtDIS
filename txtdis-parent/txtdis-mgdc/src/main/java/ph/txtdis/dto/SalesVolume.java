package ph.txtdis.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import ph.txtdis.type.UomType;

@Data
public class SalesVolume implements Keyed<Long>, Comparable<SalesVolume> {

	private Long id;

	private String seller, channel, customer, category, productLine, item;

	private BigDecimal qty, vol;
	
	private LocalDate orderDate;

	private UomType uom;

	public int compareTo(SalesVolume v) {
		return getId().compareTo(v.getId());
	}
}
