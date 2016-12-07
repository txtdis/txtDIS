package ph.txtdis.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SalesRevenue implements Comparable<SalesRevenue>, Keyed<Long>, SellerSold {

	private Long id;

	private String seller, customer;

	private BigDecimal value;

	@Override
	public int compareTo(SalesRevenue v) {
		return getId().compareTo(v.getId());
	}
}
