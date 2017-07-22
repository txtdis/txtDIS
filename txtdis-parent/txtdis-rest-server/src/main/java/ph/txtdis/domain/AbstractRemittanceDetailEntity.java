package ph.txtdis.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractRemittanceDetailEntity //
		extends AbstractKeyedEntity<Long> {

	private static final long serialVersionUID = 8157120106508577173L;

	@Column(name = "payment")
	private BigDecimal paymentValue;
}
