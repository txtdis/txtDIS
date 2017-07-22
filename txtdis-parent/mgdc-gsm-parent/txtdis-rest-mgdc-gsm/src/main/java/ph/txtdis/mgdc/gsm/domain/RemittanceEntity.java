package ph.txtdis.mgdc.gsm.domain;

import static javax.persistence.CascadeType.ALL;

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
import ph.txtdis.domain.AbstractRemittanceEntity;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "remittance", //
		indexes = { //
				@Index(columnList = "created_on, id"), //
				@Index(columnList = "deposited_on, check_id, payment_date"), //
				@Index(columnList = "drawee_bank_id, check_id"), //
				@Index(columnList = "payment_date"), //
				@Index(columnList = "received_on, check_id, payment_date") }, //
		uniqueConstraints = @UniqueConstraint(columnNames = { "id", "check_id", "drawee_bank_id" }))
public class RemittanceEntity //
		extends AbstractRemittanceEntity {

	private static final long serialVersionUID = -5860334462169889589L;

	private String collector;

	@ManyToOne
	@JoinColumn(name = "drawee_bank_id")
	private CustomerEntity draweeBank;

	@ManyToOne
	@JoinColumn(name = "depositor_bank_id")
	private CustomerEntity depositorBank;

	@OneToMany(mappedBy = "remittance", cascade = ALL)
	private List<RemittanceDetailEntity> details;

	@Column(name = "received_by")
	private String receivedBy;

	@Column(name = "received_on")
	private ZonedDateTime receivedOn;

	@Override
	public String toString() {
		return "Remittance No. " + getId();
	}
}
