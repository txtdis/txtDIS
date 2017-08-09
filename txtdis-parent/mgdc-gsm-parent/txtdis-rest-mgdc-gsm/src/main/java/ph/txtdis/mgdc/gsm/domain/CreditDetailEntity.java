package ph.txtdis.mgdc.gsm.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractDecisionNeededValidatedCreatedKeyedEntity;
import ph.txtdis.dto.CustomerCredit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "credit_detail", //
	uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "start_date"}))
public class CreditDetailEntity //
	extends AbstractDecisionNeededValidatedCreatedKeyedEntity<Long> //
	implements CustomerCredit {

	private static final long serialVersionUID = 1655742390812653142L;

	@Column(name = "term")
	private int termInDays;

	@Column(name = "grace_period")
	private int gracePeriodInDays;

	@Column(name = "credit_limit", precision = 9, scale = 2)
	private BigDecimal creditLimit;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@Override
	public int compareTo(CustomerCredit o) {
		return compareStartDates(this, o);
	}

	@Override
	public String toString() {
		return "term=" + termInDays + ", grace=" + gracePeriodInDays + ", limit=" + creditLimit + ", start=" + startDate;
	}
}
