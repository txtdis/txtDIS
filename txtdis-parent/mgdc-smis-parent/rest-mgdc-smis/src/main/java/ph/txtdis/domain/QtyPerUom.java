package ph.txtdis.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.UomType;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "qty_per_uom", //
		uniqueConstraints = @UniqueConstraint(columnNames = { "item_id", "uom" }) )
public class QtyPerUom extends CreationTracked<Long> {

	private static final long serialVersionUID = 3802256527344044201L;

	@Column(nullable = false)
	private UomType uom;

	@Column(nullable = false, precision = 8, scale = 4)
	private BigDecimal qty;

	@Column(name = "is_purchased")
	private Boolean purchased;

	@Column(name = "is_sold")
	private Boolean sold;

	@Column(name = "is_reported")
	private Boolean reported;
}
