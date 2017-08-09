package ph.txtdis.mgdc.gsm.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import ph.txtdis.domain.AbstractDecisionNeededValidatedCreatedKeyedEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

@Data
@Entity
@Table(name = "credit_note")
@EqualsAndHashCode(callSuper = true)
public class CreditNoteEntity //
	extends AbstractDecisionNeededValidatedCreatedKeyedEntity<Long> {

	private static final long serialVersionUID = 2699772316025361878L;

	@LastModifiedBy
	@Column(name = "last_modified_by")
	protected String lastModifiedBy;

	@LastModifiedDate
	@Column(name = "last_modified_on")
	protected ZonedDateTime lastModifiedOn;

	@OneToMany(cascade = ALL)
	@JoinColumn(name = "credit_note_id")
	private List<CreditNotePaymentEntity> payments;

	@Column(name = "credit_date", nullable = false)
	private LocalDate creditDate;

	@Column(nullable = false)
	private String reference, description;

	@Column(name = "total", nullable = false, precision = 10, scale = 2)
	private BigDecimal totalValue;

	@Column(name = "balance", nullable = false, precision = 10, scale = 2)
	private BigDecimal balanceValue;

	@Override
	public String toString() {
		return toDateDisplay(creditDate) + ": " + reference + " - " + description;
	}
}
