package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class EdmsAbstractMaster extends EdmsAbstractModifiedCreatedId {

	@Column(name = "xCode")
	private String code;

	@Column(name = "xName")
	private String name;
}
