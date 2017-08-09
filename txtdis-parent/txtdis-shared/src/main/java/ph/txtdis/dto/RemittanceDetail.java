package ph.txtdis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.ModuleType;

import java.math.BigDecimal;
import java.time.LocalDate;

import static ph.txtdis.type.ModuleType.DELIVERY_REPORT;
import static ph.txtdis.type.ModuleType.INVOICE;
import static ph.txtdis.util.NumberUtils.isNegative;

@Data
@EqualsAndHashCode(callSuper = true)
public class RemittanceDetail //
	extends AbstractKeyed<Long> //
	implements Keyed<Long>,
	Typed {

	private boolean isSelected;

	private String orderNo, referenceNo, customer;

	private LocalDate dueDate;

	private BigDecimal totalDueValue, paymentValue;

	@Override
	public ModuleType type() {
		return isNegative(orderNo) ? DELIVERY_REPORT : INVOICE;
	}
}
