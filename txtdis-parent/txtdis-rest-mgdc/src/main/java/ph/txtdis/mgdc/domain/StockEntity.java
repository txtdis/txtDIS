package ph.txtdis.mgdc.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractKeyedEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

@Data
@Entity
@Table(name = "stock")
@EqualsAndHashCode(callSuper = true)
public class StockEntity //
	extends AbstractKeyedEntity<Long> {

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
			return "";
		return "Stocks of " + item + " as of " + toDateDisplay(LocalDate.now()) + ": " + goodQty;
	}
}
