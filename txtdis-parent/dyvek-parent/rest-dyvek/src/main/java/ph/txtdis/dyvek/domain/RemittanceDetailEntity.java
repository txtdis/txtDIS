package ph.txtdis.dyvek.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractKeyedEntity;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import static javax.persistence.CascadeType.ALL;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "remittance_detail", //
	indexes = { //
		@Index(name = "remittance_detail_billing_id_idx", columnList = "billing_id"), //
		@Index(name = "remittance_detail_cash_advance_id_idx", columnList = "cash_advance_id"), //
		@Index(name = "remittance_detail_remittance_id_idx", columnList = "remittance_id")})
public class RemittanceDetailEntity //
	extends AbstractKeyedEntity<Long> {

	private static final long serialVersionUID = 7436394900692458958L;

	@ManyToOne(optional = false, cascade = ALL)
	private RemittanceEntity remittance;

	@ManyToOne(optional = false, cascade = ALL)
	private BillableEntity billing;
}
