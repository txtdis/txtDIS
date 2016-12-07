package ph.txtdis.dto;

import java.math.BigDecimal;

import org.apache.commons.lang3.math.Fraction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;

@Data
@EqualsAndHashCode(callSuper = true)
public class StockTakeDetail extends AbstractId<Long> implements Keyed<Long> {

	private String name;

	private UomType uom;

	private BigDecimal qty;

	private QualityType quality;

	private int qtyPerCase;

	public BigDecimal getQty() {
		return qty == null ? BigDecimal.ZERO : qty;
	}

	@JsonIgnore
	public Fraction getQtyInFractions() {
		return Fraction.getFraction(getQty().intValue(), qtyPerCase);
	}
}
