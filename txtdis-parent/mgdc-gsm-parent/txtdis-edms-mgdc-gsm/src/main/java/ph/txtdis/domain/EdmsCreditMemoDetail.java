package ph.txtdis.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "tr_cm_details")
@EqualsAndHashCode(callSuper = true)
public class EdmsCreditMemoDetail extends
		EdmsAbstractDiscountTotalTransactionCodeItemNameCostPriceUomQtyReferenceItemCodeIdDetail implements Serializable {

	private static final long serialVersionUID = 6256044418218982063L;

	@Column(name = "deliveredQty")
	private BigDecimal deliveredQty;
}
