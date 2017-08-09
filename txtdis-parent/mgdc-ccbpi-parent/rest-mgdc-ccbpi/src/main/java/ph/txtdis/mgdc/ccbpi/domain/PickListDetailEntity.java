package ph.txtdis.mgdc.ccbpi.domain;

import static javax.persistence.CascadeType.ALL;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractKeyedEntity;

@Data
@Entity
@Table(name = "picking_detail")
@EqualsAndHashCode(callSuper = true)
public class PickListDetailEntity //
	extends AbstractKeyedEntity<Long> //
	implements ItemQuantifiedEntityDetail {

	private static final long serialVersionUID = -6364730743883822721L;

	@ManyToOne(optional = false, cascade = ALL)
	private PickListEntity picking;

	@ManyToOne(optional = false)
	private ItemEntity item;

	@Column(name = "picked_qty")
	private int pickedCount;

	@Column(name = "returned_qty")
	private int returnedCount;

	@Override
	public BigDecimal getFinalQty() {
		return new BigDecimal(pickedCount - returnedCount);
	}

	@Override
	public BigDecimal getInitialQty() {
		return new BigDecimal(pickedCount);
	}

	@Override
	public BigDecimal getReturnedQty() {
		return new BigDecimal(returnedCount);
	}

	@Override
	public String toString() {
		return (pickedCount - returnedCount) + " " + item.getName();
	}
}
