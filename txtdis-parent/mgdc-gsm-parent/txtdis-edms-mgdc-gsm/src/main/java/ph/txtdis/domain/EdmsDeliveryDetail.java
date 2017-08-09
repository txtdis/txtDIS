package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "tr_dr_details")
@EqualsAndHashCode(callSuper = true)
public class EdmsDeliveryDetail //
	extends EdmsAbstractBillingDetail //
	implements EdmsDetailBased,
	Serializable {

	private static final long serialVersionUID = -8752420898579725275L;
}
