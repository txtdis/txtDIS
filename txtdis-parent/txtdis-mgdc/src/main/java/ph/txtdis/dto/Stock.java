package ph.txtdis.dto;

import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Stock extends AbstractKeyed<Long> {

	private String item;

	private BigDecimal goodQty, badQty, soldQty, priceValue;

	@Override
	public String toString() {
		if (item == null || goodQty == null)
			return null;
		return "Stocks of " + item + " as of " + toDateDisplay(LocalDate.now()) + ": " + goodQty;
	}
}
