package ph.txtdis.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class EdmsAbstractTransferredModifiedNotedWarehousedDatedRemarkedReferencedStatusCreatedId
	extends EdmsAbstractModifiedNotedWarehousedDatedRemarkedReferencedStatusCreatedId {

	@Column(name = "transferType")
	private String transferCode;
}
