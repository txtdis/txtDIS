package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class EdmsAbstractCreatedId extends EdmsAbstractId {

	@Column(name = "createBy")
	private String createdBy;

	@Column(name = "dateCreate")
	private String createdOn;
}
