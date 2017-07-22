package ph.txtdis.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.apache.commons.lang3.math.Fraction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;

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

	public BigDecimal getStartQty() {
		return startQty == null ? BigDecimal.ZERO : startQty;
	}

	public BigDecimal getInQty() {
		return inQty == null ? BigDecimal.ZERO : inQty;
	}

	public BigDecimal getOutQty() {
		return outQty == null ? BigDecimal.ZERO : outQty;
	}

	public BigDecimal getEndQty() {
		return getStartQty().add(getInQty()).subtract(getOutQty());
	}

	public BigDecimal getActualQty() {
		return actualQty == null ? BigDecimal.ZERO : actualQty;
	}

	public BigDecimal getVarianceQty() {
		return getActualQty().subtract(getEndQty());
	}

	public BigDecimal getFinalQty() {
		return finalQty == null ? BigDecimal.ZERO : finalQty;
	}

	@JsonIgnore
	public Fraction getStartQtyInFractions() {
		return Fraction.getFraction(getStartQty().intValue(), qtyPerCase);
	}

	@JsonIgnore
	public Fraction getInQtyInFractions() {
		return Fraction.getFraction(getInQty().intValue(), qtyPerCase);
	}

	@JsonIgnore
	public Fraction getOutQtyInFractions() {
		return Fraction.getFraction(getOutQty().intValue(), qtyPerCase);
	}

	@JsonIgnore
	public Fraction getEndQtyInFractions() {
		return Fraction.getFraction(getEndQty().intValue(), qtyPerCase);
	}

	@JsonIgnore
	public Fraction getActualQtyInFractions() {
		return Fraction.getFraction(getActualQty().intValue(), qtyPerCase);
	}

	@JsonIgnore
	public Fraction getVarianceQtyInFractions() {
		return Fraction.getFraction(getVarianceQty().intValue(), qtyPerCase);
	}

	@JsonIgnore
	public Fraction getFinalQtyInFractions() {
		return Fraction.getFraction(getFinalQty().intValue(), qtyPerCase);
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
