package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "ref_truck")
@EqualsAndHashCode(callSuper = true)
public class EdmsTruck //
	extends EdmsAbstractMaster //
	implements Serializable {

	private static final long serialVersionUID = -2645700673137405555L;

	@Column(name = "remarks")
	private String description;
}
