package ph.txtdis.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SalesRevenue {

	private Long id;

	private String customer;

	private BigDecimal value;
}
