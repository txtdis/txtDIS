package ph.txtdis.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class EdmsAbstractDiscountTotalTransactionCodeItemNameCostPriceUomQtyReferenceItemCodeIdDetail
		extends EdmsAbstractItemNameCostPriceUomQtyReferenceItemCodeIdDetail {

	@Column(name = "discAmount")
	private BigDecimal discountValue;

	@Column(name = "total")
	private BigDecimal totalValue;

	@Column(name = "transType")
	private String transactionCode;
}
