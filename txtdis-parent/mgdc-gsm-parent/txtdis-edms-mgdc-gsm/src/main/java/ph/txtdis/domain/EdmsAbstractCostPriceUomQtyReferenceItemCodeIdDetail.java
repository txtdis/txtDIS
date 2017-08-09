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
public abstract class EdmsAbstractCostPriceUomQtyReferenceItemCodeIdDetail //
	extends EdmsAbstractUomQtyReferencedItemCodeIdDetail {

	@Column(name = "cost")
	private BigDecimal costValue = ZERO;

	@Column(name = "price")
	private BigDecimal priceValue = ZERO;
}
