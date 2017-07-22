package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class EdmsAbstractModifiedCreatedId //
		extends EdmsAbstractCreatedId {

	@Column(name = "modiBy")
	private String modifiedBy = "";

	@Column(name = "dateModi")
	private String modifiedOn = "";
}
