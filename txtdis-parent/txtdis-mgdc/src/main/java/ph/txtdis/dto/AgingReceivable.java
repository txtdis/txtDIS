package ph.txtdis.dto;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

import lombok.Data;

@Data
public class AgingReceivable implements Keyed<Long>, SellerSold {

	private Long id;

	private String seller, customer;

	private BigDecimal currentValue = ZERO;

	private BigDecimal oneToSevenValue = ZERO;

	private BigDecimal eightToFifteenValue = ZERO;

	private BigDecimal sixteenToThirtyValue = ZERO;

	private BigDecimal greaterThanThirtyValue = ZERO;

	private BigDecimal agingValue = ZERO;

	private BigDecimal totalValue = ZERO;
}