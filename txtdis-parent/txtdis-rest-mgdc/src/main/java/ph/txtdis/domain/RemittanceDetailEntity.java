package ph.txtdis.domain;

import static javax.persistence.CascadeType.ALL;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "remittance_detail")
@EqualsAndHashCode(callSuper = true)
public class RemittanceDetailEntity extends AbstractEntityId<Long> {

	private static final long serialVersionUID = -968954060788678059L;

	@ManyToOne(optional = false, cascade = ALL)
	private RemittanceEntity remittance;

	@ManyToOne(optional = false, cascade = ALL)
	private BillableEntity billing;

	@Column(name = "payment")
	private BigDecimal paymentValue;

	@Override
	public String toString() {
		return remittance + ": " + billing + "value, " + paymentValue + "paid";
	}
}
