package ph.txtdis.mgdc.gsm.domain;

import static javax.persistence.CascadeType.ALL;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractDecisionNeededValidatedCreatedKeyedEntity;

@Data
@Entity
@Table(name = "credit_note")
@EqualsAndHashCode(callSuper = true)
public class CreditNoteEntity //
		extends AbstractDecisionNeededValidatedCreatedKeyedEntity<Long> {

	private static final long serialVersionUID = 2699772316025361878L;

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

	@LastModifiedBy
	@Column(name = "last_modified_by")
	protected String lastModifiedBy;

	@LastModifiedDate
	@Column(name = "last_modified_on")
	protected ZonedDateTime lastModifiedOn;

	@Override
	public String toString() {
		return toDateDisplay(creditDate) + ": " + reference + " - " + description;
	}
}
