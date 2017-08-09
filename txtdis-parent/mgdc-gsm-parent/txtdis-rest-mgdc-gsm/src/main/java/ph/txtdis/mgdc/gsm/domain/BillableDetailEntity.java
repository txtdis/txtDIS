package ph.txtdis.mgdc.gsm.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractKeyedEntity;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;
import ph.txtdis.util.NumberUtils;

import javax.persistence.*;
import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

@Data
@Entity
@Table(name = "billing_detail", //
	uniqueConstraints = @UniqueConstraint(columnNames = {"billing_id", "item_id", "uom", "price"}))
@EqualsAndHashCode(callSuper = true)
public class BillableDetailEntity //
	extends AbstractKeyedEntity<Long> //
	implements ItemQuantifiedEntityDetail {

	private static final long serialVersionUID = 257754573072417395L;

	@ManyToOne(optional = false)
	private BillableEntity billing;

	@ManyToOne(optional = false)
	private ItemEntity item;

	@Column(nullable = false)
	private UomType uom;

	@Column(name = "initial_qty", precision = 12, scale = 4)
	private BigDecimal initialQty;

	@Column(name = "returned_qty", precision = 12, scale = 4)
	private BigDecimal returnedQty;

	@Column(name = "sold_qty", precision = 12, scale = 4)
	private BigDecimal soldQty;

	@Column(nullable = false)
	private QualityType quality;

	@Column(name = "price")
	private BigDecimal priceValue;

	@Override
	public BigDecimal getFinalQty() {
		return getInitialQty() //
			.subtract(getSoldQty()) //
			.subtract(getReturnedQty());
	}

	@Override
	public BigDecimal getInitialQty() {
		return initialQty == null ? ZERO : initialQty;
	}

	public BigDecimal getSoldQty() {
		return soldQty == null ? ZERO : soldQty;
	}

	@Override
	public BigDecimal getReturnedQty() {
		return returnedQty == null ? ZERO : returnedQty;
	}

	public Long getItemId() {
		return item == null ? null : item.getId();
	}

	public String getItemQuality() {
		return quality + ":" + item.getName();
	}

	@Override
	public String toString() {
		return "\n" + billing.getOrderNo() + ": " + item //
			+ " -i=" + getInitialQty() //
			+ " -r=" + returnedQty //
			+ " -s=" + getSoldQty() //
			+ NumberUtils.toCurrencyText(priceValue);
	}
}
