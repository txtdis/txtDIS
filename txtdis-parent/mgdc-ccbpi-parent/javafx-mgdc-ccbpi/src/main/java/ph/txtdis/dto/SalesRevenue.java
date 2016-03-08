package ph.txtdis.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SalesRevenue implements Keyed<Long> {

	private Long id;

	private String customer;

	private BigDecimal value;
}
