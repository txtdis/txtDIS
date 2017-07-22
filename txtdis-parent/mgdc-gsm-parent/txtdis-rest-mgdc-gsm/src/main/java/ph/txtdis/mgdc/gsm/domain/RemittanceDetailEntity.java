package ph.txtdis.mgdc.gsm.domain;

import static javax.persistence.CascadeType.ALL;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractRemittanceDetailEntity;

@Data
@Entity
@Table(name = "remittance_detail")
@EqualsAndHashCode(callSuper = true)
public class RemittanceDetailEntity //
		extends AbstractRemittanceDetailEntity {

	private static final long serialVersionUID = -968954060788678059L;

	@ManyToOne(optional = false, cascade = ALL)
	private RemittanceEntity remittance;

	@ManyToOne(optional = false, cascade = ALL)
	private BillableEntity billing;

	@Override
	public String toString() {
		return remittance + " for " + billing;
	}
}
