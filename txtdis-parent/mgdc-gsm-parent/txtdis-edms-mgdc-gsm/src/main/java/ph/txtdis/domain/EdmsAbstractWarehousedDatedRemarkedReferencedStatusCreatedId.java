package ph.txtdis.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class EdmsAbstractWarehousedDatedRemarkedReferencedStatusCreatedId //
	extends EdmsAbstractDatedRemarkedReferencedStatusCreatedId {

	@Column(name = "warehouse")
	private String warehouseCode;
}
