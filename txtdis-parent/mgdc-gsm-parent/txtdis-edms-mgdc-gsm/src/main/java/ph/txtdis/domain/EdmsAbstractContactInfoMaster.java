package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class EdmsAbstractContactInfoMaster //
		extends EdmsAbstractMaster {

	@Column(name = "contactNo")
	private String phone;

	@Column(name = "address")
	private String address;
}
