package ph.txtdis.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class EdmsAbstractVolumeDiscountTotalTransactionCodeItemNameCostPriceUomQtyReferenceItemCodeIdDetail
		extends EdmsAbstractDiscountTotalTransactionCodeItemNameCostPriceUomQtyReferenceItemCodeIdDetail {

	@Column(name = "otherDisc")
	private BigDecimal totalDiscountValue;
}
