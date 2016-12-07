package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "tr_ilr_header")
@EqualsAndHashCode(callSuper = true)
public class EdmsIncomingLoad extends EdmsAbstractRemittanceVarianceBasisModifiedNotedTransported
		implements Serializable {

	private static final long serialVersionUID = -1560466383746242501L;

	@Override
	public String toString() {
		return getReferenceNo() + ": " + getTruckCode() + ", " + getOrderDate();
	}
}
