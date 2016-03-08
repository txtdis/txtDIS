package ph.txtdis.domain;

import static javax.persistence.CascadeType.ALL;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static java.math.BigDecimal.ZERO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;

@Data
@Entity
@Table(name = "billing_detail", //
		indexes = { @Index(columnList = "billing_id, item_id") }, //
		uniqueConstraints = @UniqueConstraint(columnNames = { "billing_id", "item_id" }) )
@EqualsAndHashCode(callSuper = true)
public class BillingDetail extends AbstractId<Long> {

	private static final long serialVersionUID = 257754573072417395L;

	@ManyToOne(optional = false, cascade = ALL)
	private Billing billing;

	@ManyToOne(optional = false)
	private Item item;

	@Column(nullable = false)
	private UomType uom;

	@JsonIgnore
	@Column(name = "initial_qty", nullable = false, precision = 12, scale = 4)
	private BigDecimal initialQty;

	@JsonIgnore
	@Column(name = "returned_qty", precision = 12, scale = 4)
	private BigDecimal returnedQty;

	@Column(nullable = false)
	private QualityType quality;

	@Column(name = "price", nullable = false, precision = 8, scale = 2)
	private BigDecimal priceValue;

	@Column(name = "on_purchase_days_level")
	private Integer onPurchaseDaysLevel;

	@Column(name = "on_receipt_days_level")
	private Integer onReceiptDaysLevel;

	public Long getItemId() {
		return item == null ? null : item.getId();
	}

	public BigDecimal getUnitQty() {
		return netQty().multiply(qtyPerPiece());
	}

	@Override
	public String toString() {
		return "Billing Detail No. " + id;
	}

	private BigDecimal initialQty() {
		return initialQty == null ? ZERO : initialQty;
	}

	private BigDecimal netQty() {
		return initialQty().subtract(returnedQty());
	}

	private BigDecimal qtyPerPiece() {
		try {
			return item.getQtyPerUomList().stream().filter(q -> q.getUom() == uom).findAny().get().getQty();
		} catch (Exception e) {
			return ZERO;
		}
	}

	private BigDecimal returnedQty() {
		return returnedQty == null ? ZERO : returnedQty;
	}
}
