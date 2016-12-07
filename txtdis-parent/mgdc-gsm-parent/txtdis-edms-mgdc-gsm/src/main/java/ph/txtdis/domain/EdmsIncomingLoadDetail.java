package ph.txtdis.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.util.NumberUtils;

@Data
@Entity
@Table(name = "tr_ilr_details")
@EqualsAndHashCode(callSuper = true)
public class EdmsIncomingLoadDetail extends EdmsAbstractReferencedItemCodeIdDetail implements Serializable {

	private static final long serialVersionUID = -2615910955094613912L;

	@Column(name = "itemName")
	private String itemName;

	@Column(name = "fullCase")
	private BigDecimal fullCaseQty;

	@Column(name = "fullBottle")
	private BigDecimal fullBottleQty;

	@Column(name = "returnCase")
	private BigDecimal returnedCaseQty;

	@Column(name = "returnBottle")
	private BigDecimal returnedBottleQty;

	@Column(name = "empCase")
	private BigDecimal emptyCaseQty;

	@Column(name = "empBottle")
	private BigDecimal emptyBottleQty;

	@Column(name = "psCase")
	private BigDecimal promoCaseQty;

	@Column(name = "psBtottle")
	private BigDecimal promoBottleQty;

	public BigDecimal getFullCaseQty() {
		return fullCaseQty == null ? BigDecimal.ZERO : fullCaseQty;
	}

	public BigDecimal getFullBottleQty() {
		return fullBottleQty == null ? BigDecimal.ZERO : fullBottleQty;
	}

	public BigDecimal getReturnedCaseQty() {
		return returnedCaseQty == null ? BigDecimal.ZERO : returnedCaseQty;
	}

	public BigDecimal getReturnedBottleQty() {
		return returnedBottleQty == null ? BigDecimal.ZERO : returnedBottleQty;
	}

	@Override
	public String toString() {
		return getReferenceNo() + ": " + itemName + " - " + NumberUtils.formatWhole(fullCaseQty) + "CS"
				+ NumberUtils.formatWhole(fullBottleQty);
	}
}
