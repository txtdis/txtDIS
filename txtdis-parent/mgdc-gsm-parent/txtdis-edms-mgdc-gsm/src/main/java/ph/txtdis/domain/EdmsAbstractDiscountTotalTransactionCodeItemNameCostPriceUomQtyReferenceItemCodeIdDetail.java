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
public abstract class EdmsAbstractDiscountTotalTransactionCodeItemNameCostPriceUomQtyReferenceItemCodeIdDetail //
	extends EdmsAbstractItemNameCostPriceUomQtyReferenceItemCodeIdDetail {

	@Column(name = "discAmount")
	private BigDecimal discountValue = ZERO;

	@Column(name = "total")
	private BigDecimal totalValue;

	@Column(name = "transType")
	private String transactionCode;
}
