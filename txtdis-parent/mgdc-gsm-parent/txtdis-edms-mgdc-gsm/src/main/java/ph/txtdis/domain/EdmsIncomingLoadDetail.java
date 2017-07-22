package ph.txtdis.domain;

import static java.math.BigDecimal.ZERO;

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
	private BigDecimal fullCaseQty = ZERO;

	@Column(name = "fullBottle")
	private BigDecimal fullBottleQty = ZERO;

	@Column(name = "returnCase")
	private BigDecimal returnedCaseQty = ZERO;

	@Column(name = "returnBottle")
	private BigDecimal returnedBottleQty = ZERO;

	@Column(name = "empCase")
	private BigDecimal emptyCaseQty = ZERO;

	@Column(name = "empBottle")
	private BigDecimal emptyBottleQty = ZERO;

	@Column(name = "psCase")
	private BigDecimal promoCaseQty = ZERO;

	@Column(name = "psBtottle")
	private BigDecimal promoBottleQty = ZERO;

	@Override
	public String toString() {
		return getReferenceNo() + ": " + itemName + " - " + NumberUtils.toIdText(fullCaseQty) + "CS" + NumberUtils.toIdText(fullBottleQty);
	}
}
