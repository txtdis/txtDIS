package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "tr_logp_header")
@EqualsAndHashCode(callSuper = true)
public class EdmsLoadOrder extends EdmsAbstractRemittanceVarianceBasisModifiedNotedTransported implements Serializable {

	private static final long serialVersionUID = -1666094162685643774L;

	@Override
	public String toString() {
		return getReferenceNo() + ": " + getTruckCode() + " - " + getOrderDate();
	}
}
