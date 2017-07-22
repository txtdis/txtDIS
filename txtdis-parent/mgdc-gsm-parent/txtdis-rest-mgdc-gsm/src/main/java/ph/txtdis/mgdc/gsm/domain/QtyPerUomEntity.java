package ph.txtdis.mgdc.gsm.domain;

import static javax.persistence.CascadeType.ALL;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.mgdc.domain.AbstractQtyPerUomEntity;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "qty_per_uom", //
		uniqueConstraints = @UniqueConstraint(columnNames = { "item_id", "uom" }))
public class QtyPerUomEntity //
		extends AbstractQtyPerUomEntity {

	private static final long serialVersionUID = 3802256527344044201L;

	@ManyToOne(optional = false, cascade = ALL)
	private ItemEntity item;

	@Override
	public String toString() {
		return "QtyPerUom - " + getQty().setScale(4).toPlainString() + getUom() + " is " //
				+ (getPurchased() ? "purchased" : "") //
				+ (getSold() ? ", sold" : "") //
				+ (getReported() ? ", reported" : "") //
				+ "\n";
	}
}
