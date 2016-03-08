package ph.txtdis.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "location_tree")
@EqualsAndHashCode(callSuper = true)
public class LocationTree extends CreationTracked<Long> {

	private static final long serialVersionUID = 7452722249369420815L;

	@ManyToOne(optional = false)
	private Location location;

	@ManyToOne(optional = false)
	private Location parent;
}
