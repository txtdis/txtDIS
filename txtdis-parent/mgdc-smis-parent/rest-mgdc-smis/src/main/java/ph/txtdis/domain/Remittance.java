package ph.txtdis.domain;

import static javax.persistence.CascadeType.ALL;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(indexes = { //
		@Index(columnList = "created_on, id"), //
		@Index(columnList = "deposited_on, check_id, payment_date"), //
		@Index(columnList = "drawee_bank_id, check_id"), //
		@Index(columnList = "payment_date"), //
		@Index(columnList = "received_on, check_id, payment_date") }, //
		uniqueConstraints = @UniqueConstraint(columnNames = { "check_id", "drawee_bank_id" }) )
public class Remittance extends DecisionNeeded {

	private static final long serialVersionUID = -5860334462169889589L;

	@Column(nullable = false, precision = 8, scale = 2)
	private BigDecimal value;

	@ManyToOne
	@JoinColumn(name = "drawee_bank_id")
	private Customer draweeBank;

	@ManyToOne
	@JoinColumn(name = "depositor_bank_id")
	private Customer depositorBank;

	@OneToMany(mappedBy = "remittance", cascade = ALL)
	private List<RemittanceDetail> details;

	@Column(name = "payment_date", nullable = false)
	private LocalDate paymentDate;

	@Column(name = "check_id")
	private Long checkId;

	private String collector, depositor;

	@Column(name = "received_by")
	private String receivedBy;

	@Column(name = "deposited_on")
	private ZonedDateTime depositedOn;

	@Column(name = "received_on")
	private ZonedDateTime receivedOn;

	@Column(name = "depositor_on")
	private ZonedDateTime depositorOn;

	@Override
	public String toString() {
		return "Collection ID " + id;
	}
}
