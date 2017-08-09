package ph.txtdis.dyvek.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractRemittanceEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "remittance", //
	indexes = { //
		@Index(name = "remittance_check_id_idx", columnList = "check_id"), //
		@Index(name = "remittance_created_on_idx", columnList = "created_on"), //
		@Index(name = "remittance_decided_on_idx", columnList = "decided_on"), //
		@Index(name = "remittance_deposited_to_id_idx", columnList = "deposited_to_id"), //
		@Index(name = "remittance_drawn_from_id_idx", columnList = "drawn_from_id"), //
		@Index(name = "remittance_payment_date_idx", columnList = "payment_date")})
public class RemittanceEntity //
	extends AbstractRemittanceEntity {

	private static final long serialVersionUID = -5917301094208442644L;

	@Column(name = "received_date")
	private LocalDate receivedDate;

	@ManyToOne
	@JoinColumn(name = "received_from_id")
	private CustomerEntity receivedFrom;

	@ManyToOne
	@JoinColumn(name = "drawn_from_id")
	private CustomerEntity drawnFrom;

	@ManyToOne
	@JoinColumn(name = "deposited_to_id")
	private CustomerEntity depositedTo;

	@OneToMany(mappedBy = "remittance", cascade = ALL)
	private List<RemittanceDetailEntity> details;

	@Override
	public String toString() {
		return "Remittance No. " + getId();
	}
}
