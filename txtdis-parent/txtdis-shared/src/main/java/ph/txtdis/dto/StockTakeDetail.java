package ph.txtdis.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.math.Fraction;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class StockTakeDetail //
	extends AbstractKeyed<Long> {

	private String name;

	private UomType uom;

	private BigDecimal qty;

	private QualityType quality;

	private int qtyPerCase;

	@JsonIgnore
	public Fraction getQtyInFractions() {
		return Fraction.getFraction(getQty().intValue(), qtyPerCase);
	}

	public BigDecimal getQty() {
		return qty == null ? BigDecimal.ZERO : qty;
	}
}
