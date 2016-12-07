package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "picking_detail")
@EqualsAndHashCode(callSuper = true)
public class PickListDetailEntity extends AbstractEntityId<Long> {

	private static final long serialVersionUID = -6364730743883822721L;

	@ManyToOne(optional = false)
	private ItemEntity item;

	@Column(name = "picked_qty")
	private int pickedQty;

	@Column(name = "returned_qty")
	private int returnedQty;
}
