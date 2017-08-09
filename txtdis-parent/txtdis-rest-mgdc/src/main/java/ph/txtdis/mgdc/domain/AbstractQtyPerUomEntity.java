package ph.txtdis.mgdc.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractCreatedKeyedEntity;
import ph.txtdis.type.UomType;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractQtyPerUomEntity //
	extends AbstractCreatedKeyedEntity<Long> {

	private static final long serialVersionUID = 8164715573241790785L;

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
