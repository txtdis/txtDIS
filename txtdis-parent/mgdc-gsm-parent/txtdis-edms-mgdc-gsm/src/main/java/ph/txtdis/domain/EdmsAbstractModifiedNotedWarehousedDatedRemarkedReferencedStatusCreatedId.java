package ph.txtdis.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class EdmsAbstractModifiedNotedWarehousedDatedRemarkedReferencedStatusCreatedId
	extends EdmsAbstractNotedWarehousedDatedRemarkedReferencedStatusCreatedId {

	@Column(name = "modiBy")
	private String modifiedBy = "";

	@Column(name = "dateModi")
	private String modifiedOn = "";
}
