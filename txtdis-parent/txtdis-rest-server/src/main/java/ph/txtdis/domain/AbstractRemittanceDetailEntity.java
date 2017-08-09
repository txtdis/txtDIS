package ph.txtdis.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractRemittanceDetailEntity //
	extends AbstractKeyedEntity<Long> {

	private static final long serialVersionUID = 8157120106508577173L;

	@Column(name = "payment")
	private BigDecimal paymentValue;
}
