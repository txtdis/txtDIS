package ph.txtdis.domain;

import static java.math.BigDecimal.ZERO;
import static javax.persistence.CascadeType.ALL;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.Fractioned;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;
import ph.txtdis.util.NumberUtils;

@Data
@Entity
@Table(name = "billing_detail", //
		uniqueConstraints = @UniqueConstraint(columnNames = { "billing_id", "item_id", "uom", "price" }))
@EqualsAndHashCode(callSuper = true)
public class BillableDetailEntity extends AbstractEntityId<Long> implements Fractioned {

	private static final long serialVersionUID = 257754573072417395L;

	@ManyToOne(optional = false, cascade = ALL)
	private BillableEntity billing;

	@ManyToOne(optional = false)
	private ItemEntity item;

	@Column(nullable = false)
	private UomType uom;

	@Column(name = "initial_qty", precision = 12, scale = 4)
	private BigDecimal initialQty;

	@Column(name = "sold_qty", precision = 12, scale = 4)
	private BigDecimal soldQty;

	@Column(name = "returned_qty", precision = 12, scale = 4)
	private BigDecimal returnedQty;

	@Column(name = "free_qty", precision = 12, scale = 4)
	private BigDecimal freeQty;

	@Column(nullable = false)
	private QualityType quality;

	@Column(name = "price")
	private BigDecimal priceValue;

	@Column(name = "discount")
	private BigDecimal discountValue;

	@Column(name = "on_purchase_days_level")
	private Integer onPurchaseDaysLevel;

	@Column(name = "on_receipt_days_level")
	private Integer onReceiptDaysLevel;

	public Long getItemId() {
		return item == null ? null : item.getId();
	}

	public String getItemQuality() {
		return quality + ":" + item.getName();
	}

	public BigDecimal getInitialUnitQty() {
		return getInitialQty().multiply(qtyPerPiece());
	}

	public BigDecimal getUnitQty() {
		return getFinalQty().multiply(qtyPerPiece());
	}

	@Override
	public int getQtyPerCase() {
		return qtyPerUom(UomType.CS).intValue();
	}

	private BigDecimal qtyPerPiece() {
		return qtyPerUom(uom);
	}

	private BigDecimal qtyPerUom(UomType uom) {
		try {
			return item.getQtyPerUomList().stream().filter(q -> q.getUom() == uom).findAny().get().getQty();
		} catch (Exception e) {
			return ZERO;
		}
	}

	@Override
	public String toString() {
		return billing.getOrderNo() + ": " + item.getName() + " - " + initialQty + uom + ", "
				+ NumberUtils.toCurrencyText(priceValue);
	}
}
