package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class EdmsAbstractBillingDetail extends EdmsAbstractBillableDetail {

	@Column(name = "soRecId")
	private Long salesOrderDetailId;
}
