package ph.txtdis.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class AbstractRemittanceEntity //
	extends AbstractDecisionNeededValidatedCreatedKeyedEntity<Long> {

	private static final long serialVersionUID = 4975638841644224415L;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal value;

	@Column(name = "payment_date", nullable = false)
	private LocalDate paymentDate;

	@Column(name = "check_id")
	private Long checkId;

	private String depositor;

	@Column(name = "deposited_on")
	private ZonedDateTime depositedOn;

	@Column(name = "depositor_on")
	private ZonedDateTime depositorOn;
}
