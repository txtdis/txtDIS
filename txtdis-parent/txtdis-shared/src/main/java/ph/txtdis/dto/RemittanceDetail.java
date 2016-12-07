package ph.txtdis.dto;

import static ph.txtdis.type.BillableType.DELIVERY_REPORT;
import static ph.txtdis.type.BillableType.INVOICE;
import static ph.txtdis.util.NumberUtils.isNegative;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.BillableType;

@Data
@EqualsAndHashCode(callSuper = true)
public class RemittanceDetail extends AbstractId<Long> implements Keyed<Long>, Typed {

	private String orderNo, customerName;

	private LocalDate dueDate;

	private BigDecimal totalDueValue, paymentValue;

	@Override
	public BillableType type() {
		return isNegative(orderNo) ? DELIVERY_REPORT : INVOICE;
	}
}
