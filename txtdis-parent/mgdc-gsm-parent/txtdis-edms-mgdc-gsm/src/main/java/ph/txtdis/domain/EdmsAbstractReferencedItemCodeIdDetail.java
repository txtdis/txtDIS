package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class EdmsAbstractReferencedItemCodeIdDetail extends EdmsAbstractId {

	@Column(name = "refNo")
	private String referenceNo;

	@Column(name = "itemCode")
	private String itemCode;
}
