package ph.txtdis.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.CustomerCredit;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "credit_detail", uniqueConstraints = @UniqueConstraint(columnNames = { "customer_id", "start_date" }) )
public class CreditDetail extends DecisionNeeded implements CustomerCredit {

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
}
