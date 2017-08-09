package ph.txtdis.dyvek.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractCreatedKeyedEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static ph.txtdis.util.TextUtils.blankIfNull;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "cash_advance", //
	indexes = { //
		@Index(name = "cash_advance_bank_id_idx", columnList = "bank_id"), //
		@Index(name = "cash_advance_balance_idx", columnList = "balance"), //
		@Index(name = "cash_advance_check_id_idx", columnList = "check_id"), //
		@Index(name = "cash_advance_customer_id_idx", columnList = "customer_id")})
public class CashAdvanceEntity //
	extends AbstractCreatedKeyedEntity<Long> {

	private static final long serialVersionUID = -6771548241439781280L;

	@ManyToOne
	private CustomerEntity customer, bank;

	@Column(name = "check_date")
	private LocalDate checkDate;

	@Column(name = "check_id")
	private Long checkId;

	@Column(name = "value")
	private BigDecimal totalValue;

	@Column(name = "balance")
	private BigDecimal balanceValue;

	@OneToMany(cascade = ALL)
	@JoinColumn(name = "cash_advance_id")
	private List<RemittanceDetailEntity> liquidations;

	@Override
	public String toString() {
		return blankIfNull(getId());
	}
}
