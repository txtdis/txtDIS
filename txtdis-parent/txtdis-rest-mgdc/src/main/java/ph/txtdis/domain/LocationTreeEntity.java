package ph.txtdis.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "location_tree")
@EqualsAndHashCode(callSuper = true)
public class LocationTreeEntity extends AbstractCreatedEntity<Long> {

	private static final long serialVersionUID = 7452722249369420815L;

	@ManyToOne(optional = false)
	private LocationEntity location;

	@ManyToOne(optional = false)
	private LocationEntity parent;
}
