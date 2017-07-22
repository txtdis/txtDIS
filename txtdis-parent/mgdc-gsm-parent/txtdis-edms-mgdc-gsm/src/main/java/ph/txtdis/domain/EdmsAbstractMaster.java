package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.Named;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class EdmsAbstractMaster //
		extends EdmsAbstractModifiedCreatedId //
		implements Named {

	@Column(name = "xCode")
	private String code;

	@Column(name = "xName")
	private String name;
}
