package ph.txtdis.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;

@Data
@Entity
@Table(name = "stock_take_variance")
@EqualsAndHashCode(callSuper = true)
public class StockTakeVarianceEntity extends AbstractEntityId<Long> {

	private static final long serialVersionUID = 9092668909841849439L;

	@Column(name = "count_date", nullable = false)
	private LocalDate countDate;

	@ManyToOne(optional = false)
	private ItemEntity item;

	@Column(nullable = false)
	private UomType uom;

	@Column(nullable = false)
	private QualityType quality;

	@Column(name = "beginning", nullable = false, precision = 10, scale = 4)
	private BigDecimal startQty;

	@Column(name = "incoming", nullable = false, precision = 10, scale = 4)
	private BigDecimal inQty;

	@Column(name = "outgoing", nullable = false, precision = 10, scale = 4)
	private BigDecimal outQty;

	@Column(name = "actual", nullable = false, precision = 10, scale = 4)
	private BigDecimal actualQty;

	@Column(name = "final", precision = 10, scale = 4)
	private BigDecimal finalQty;

	private String justification;

	public BigDecimal getStartQty() {
		return startQty == null ? BigDecimal.ZERO : startQty;
	}

	public BigDecimal getInQty() {
		return inQty == null ? BigDecimal.ZERO : inQty;
	}

	public BigDecimal getOutQty() {
		return outQty == null ? BigDecimal.ZERO : outQty;
	}

	public BigDecimal getActualQty() {
		return actualQty == null ? BigDecimal.ZERO : actualQty;
	}

	public BigDecimal getFinalQty() {
		return finalQty == null ? BigDecimal.ZERO : finalQty;
	}

	@Override
	public String toString() {
		return getCountDate() + ": " + getItem().getName() + ", " + getQuality() //
				+ ", start= " + getStartQty() + ", in= " + getInQty() + ", out= " + getOutQty() // 
				+ ", actual= " + getActualQty() + ", final= " + getFinalQty();
	}
}
