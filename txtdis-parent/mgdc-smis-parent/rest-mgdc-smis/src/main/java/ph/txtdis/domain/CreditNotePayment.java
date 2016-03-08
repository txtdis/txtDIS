package ph.txtdis.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "credit_note_payment")
@EqualsAndHashCode(callSuper = true)
public class CreditNotePayment extends AbstractId<Long> {

	private static final long serialVersionUID = -5312616572994985321L;

	@Column(name = "payment_value", nullable = false, precision = 10, scale = 2)
	private BigDecimal paymentValue;

	@Column(nullable = false)
	private String reference;

	@Column(name = "payment_date", nullable = false)
	private LocalDate paymentDate;

	@Column(name = "remarks")
	private String paymentRemarks;
}
