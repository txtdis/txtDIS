package ph.txtdis.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class EdmsAbstractPaidTransported //
		extends EdmsAbstractTransported {

	@Column(name = "paymentType")
	private String paymentMode;

	@Column(name = "totAmount")
	private BigDecimal totalValue;

	@Column(name = "totDiscount")
	private BigDecimal totalDiscountValue;

	@Column(name = "custCode")
	private String customerCode;
}
