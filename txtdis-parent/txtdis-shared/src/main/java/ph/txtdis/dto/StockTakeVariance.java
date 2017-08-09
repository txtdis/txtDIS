package ph.txtdis.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.math.Fraction;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class StockTakeVariance //
	extends AbstractKeyed<Long> {

	private LocalDate countDate;

	private String item, justification;

	private UomType uom;

	private QualityType quality;

	private BigDecimal startQty, inQty, outQty, actualQty, finalQty;

	private int qtyPerCase;

	private Boolean isValid;

	@JsonIgnore
	public Fraction getStartQtyInFractions() {
		return Fraction.getFraction(getStartQty().intValue(), qtyPerCase);
	}

	public BigDecimal getStartQty() {
		return startQty == null ? BigDecimal.ZERO : startQty;
	}

	@JsonIgnore
	public Fraction getInQtyInFractions() {
		return Fraction.getFraction(getInQty().intValue(), qtyPerCase);
	}

	public BigDecimal getInQty() {
		return inQty == null ? BigDecimal.ZERO : inQty;
	}

	@JsonIgnore
	public Fraction getOutQtyInFractions() {
		return Fraction.getFraction(getOutQty().intValue(), qtyPerCase);
	}

	public BigDecimal getOutQty() {
		return outQty == null ? BigDecimal.ZERO : outQty;
	}

	@JsonIgnore
	public Fraction getEndQtyInFractions() {
		return Fraction.getFraction(getEndQty().intValue(), qtyPerCase);
	}

	public BigDecimal getEndQty() {
		return getStartQty().add(getInQty()).subtract(getOutQty());
	}

	@JsonIgnore
	public Fraction getActualQtyInFractions() {
		return Fraction.getFraction(getActualQty().intValue(), qtyPerCase);
	}

	public BigDecimal getActualQty() {
		return actualQty == null ? BigDecimal.ZERO : actualQty;
	}

	@JsonIgnore
	public Fraction getVarianceQtyInFractions() {
		return Fraction.getFraction(getVarianceQty().intValue(), qtyPerCase);
	}

	public BigDecimal getVarianceQty() {
		return getActualQty().subtract(getEndQty());
	}

	@JsonIgnore
	public Fraction getFinalQtyInFractions() {
		return Fraction.getFraction(getFinalQty().intValue(), qtyPerCase);
	}

	public BigDecimal getFinalQty() {
		return finalQty == null ? BigDecimal.ZERO : finalQty;
	}

	@Override
	public String toString() {
		return getCountDate() //
			+ ": " + getItem() //
			+ ", " + getQuality() //
			+ ", start= " + getStartQty() //
			+ ", in= " + getInQty() //
			+ ", out= " + getOutQty() //
			+ ", actual= " + getActualQty() //
			+ ", final= " + getFinalQty();
	}
}
