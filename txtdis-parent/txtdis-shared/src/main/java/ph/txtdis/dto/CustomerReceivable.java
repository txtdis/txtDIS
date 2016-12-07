package ph.txtdis.dto;

import static ph.txtdis.type.BillableType.DELIVERY_REPORT;
import static ph.txtdis.type.BillableType.INVOICE;
import static ph.txtdis.util.NumberUtils.isNegative;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import ph.txtdis.type.BillableType;

@Data
public class CustomerReceivable implements Keyed<Long>, Typed {

	private Long id;

	private String orderNo;

	private LocalDate orderDate, dueDate;

	private BigDecimal unpaidValue, totalValue;

	private int daysOverCount;

	@Override
	public BillableType type() {
		return isNegative(orderNo) ? DELIVERY_REPORT : INVOICE;
	}
}