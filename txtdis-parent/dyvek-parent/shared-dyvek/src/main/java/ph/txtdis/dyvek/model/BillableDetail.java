package ph.txtdis.dyvek.model;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.AbstractKeyed;
import ph.txtdis.dto.Keyed;

@Data
@EqualsAndHashCode(callSuper = true)
public class BillableDetail //
		extends AbstractKeyed<Long> //
		implements Keyed<Long> {

	private LocalDate orderDate;

	private String orderNo, customer, item;

	private BigDecimal qty, //
			priceValue = ZERO, //
			assignedQty = ZERO;

	public BigDecimal getValue() {
		return priceValue.multiply(qty);
	}
}
