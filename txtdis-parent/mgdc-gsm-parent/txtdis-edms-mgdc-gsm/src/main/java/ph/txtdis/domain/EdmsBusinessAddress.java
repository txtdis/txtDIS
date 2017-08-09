package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "ref_businessaddress")
@EqualsAndHashCode(callSuper = true)
public class EdmsBusinessAddress
	extends EdmsAbstractId
	implements Serializable {

	private static final long serialVersionUID = 6503209040701208758L;

	@Column(name = "prov")
	private String province;

	@Column(name = "city")
	private String city;

	@Column(name = "brgy")
	private String barangay;
}
