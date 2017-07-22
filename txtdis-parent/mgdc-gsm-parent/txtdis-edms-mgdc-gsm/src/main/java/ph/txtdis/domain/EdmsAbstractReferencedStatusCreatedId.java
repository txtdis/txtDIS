package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class EdmsAbstractReferencedStatusCreatedId //
		extends EdmsAbstractCreatedId //
		implements Referenced {

	@Column(name = "refNo")
	private String referenceNo;

	@Column(name = "stat")
	private String status;
}
