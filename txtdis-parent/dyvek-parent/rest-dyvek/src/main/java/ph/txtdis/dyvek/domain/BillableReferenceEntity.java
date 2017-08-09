package ph.txtdis.dyvek.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractKeyedEntity;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "billing_reference", //
	indexes = { //
		@Index(name = "billing_reference_billing_id_idx", columnList = "billing_id"), //
		@Index(name = "billing_reference_delivery_id_idx", columnList = "delivery_id"), //
		@Index(name = "billing_reference_reference_id_idx", columnList = "reference_id")})
public class BillableReferenceEntity //
	extends AbstractKeyedEntity<Long> {

	private static final long serialVersionUID = 4156281751849073994L;

	@ManyToOne
	private BillableEntity billing;

	@ManyToOne(optional = false)
	private BillableEntity delivery;

	@ManyToOne(optional = false)
	private BillableEntity reference;

	@Column(nullable = false)
	private BigDecimal qty;

	@Override
	public String toString() {
		return "\nb=" + billing + ",\nd=" + delivery + ",\nr=" + reference + ",\nqty=" + qty + "\n";
	}
}
