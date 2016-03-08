package ph.txtdis.domain;

import static javax.persistence.CascadeType.ALL;

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

import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "credit_note")
@EqualsAndHashCode(callSuper = true)
public class CreditNote extends CreationTracked<Long> {

	private static final long serialVersionUID = 2699772316025361878L;

	@OneToMany(cascade = ALL)
	@JoinColumn(name = "credit_note_id")
	private List<CreditNotePayment> payments;

	@Column(name = "credit_date", nullable = false)
	private LocalDate creditDate;

	@Column(nullable = false)
	private String description;

	@Column(name = "total", nullable = false, precision = 10, scale = 2)
	private BigDecimal totalValue;

	@Column(name = "balance", nullable = false, precision = 10, scale = 2)
	private BigDecimal balanceValue;

	private String remarks;

	@LastModifiedBy
	@Column(name = "last_modified_by")
	protected String lastModifiedBy;

	@LastModifiedDate
	@Column(name = "last_modified_on")
	protected ZonedDateTime lastModifiedOn;

	@Override
	public String toString() {
		return toDateDisplay(creditDate) + " - " + description;
	}
}
