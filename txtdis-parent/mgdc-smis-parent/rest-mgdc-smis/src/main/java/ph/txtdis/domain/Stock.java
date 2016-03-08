package ph.txtdis.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;

import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Stock extends AbstractId<Long> {

	private static final long serialVersionUID = 257754573072417395L;

	private String item;

	@Column(name = "good_qty", precision = 10, scale = 4)
	private BigDecimal goodQty;

	@Column(name = "bad_qty", precision = 10, scale = 4)
	private BigDecimal badQty;

	@Column(name = "sold_qty", precision = 12, scale = 4)
	private BigDecimal soldQty;

	@Column(name = "price", precision = 10, scale = 2)
	private BigDecimal priceValue;

	@Override
	public String toString() {
		if (item == null || goodQty == null)
			return null;
		return "Stocks of " + item + " as of " + toDateDisplay(LocalDate.now()) + ": " + goodQty;
	}
}
