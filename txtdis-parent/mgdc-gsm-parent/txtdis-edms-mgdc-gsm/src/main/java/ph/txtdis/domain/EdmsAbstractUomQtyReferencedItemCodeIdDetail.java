package ph.txtdis.domain;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class EdmsAbstractUomQtyReferencedItemCodeIdDetail //
	extends EdmsAbstractReferencedItemCodeIdDetail {

	@Column(name = "uomCode")
	private String uomCode;

	@Column(name = "qty")
	private BigDecimal qty = ZERO;
}
