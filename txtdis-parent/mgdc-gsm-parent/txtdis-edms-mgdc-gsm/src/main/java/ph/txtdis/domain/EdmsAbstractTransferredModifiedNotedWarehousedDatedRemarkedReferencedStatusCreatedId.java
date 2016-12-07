package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class EdmsAbstractTransferredModifiedNotedWarehousedDatedRemarkedReferencedStatusCreatedId
		extends EdmsAbstractModifiedNotedWarehousedDatedRemarkedReferencedStatusCreatedId {

	@Column(name = "transferType")
	private String transferCode;
}
