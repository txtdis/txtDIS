package ph.txtdis.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class EdmsAbstractVolumeDiscountTotalTransactionCodeItemNameCostPriceUomQtyReferenceItemCodeIdDetail //
	extends EdmsAbstractDiscountTotalTransactionCodeItemNameCostPriceUomQtyReferenceItemCodeIdDetail {

	@Column(name = "otherDisc")
	private BigDecimal totalDiscountValue = ZERO;
}
