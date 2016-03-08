package ph.txtdis.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentDetail extends AbstractId<Long> {

	private Long billingId;

	private String customerName;

	private BigDecimal totalDueValue, paymentValue;
}
