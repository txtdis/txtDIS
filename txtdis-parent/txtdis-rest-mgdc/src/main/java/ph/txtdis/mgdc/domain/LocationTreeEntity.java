package ph.txtdis.mgdc.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractCreatedKeyedEntity;

@Data
@Entity
@Table(name = "location_tree")
@EqualsAndHashCode(callSuper = true)
public class LocationTreeEntity //
		extends AbstractCreatedKeyedEntity<Long> {

	private static final long serialVersionUID = 7452722249369420815L;

	@ManyToOne(optional = false)
	private LocationEntity location;

	@ManyToOne(optional = false)
	private LocationEntity parent;

	@Override
	public String toString() {
		return getLocation() + ", " + getParent();
	}
}
