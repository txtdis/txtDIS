package ph.txtdis.mgdc.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ph.txtdis.domain.AbstractNamedCreatedKeyedEntity;
import ph.txtdis.type.LocationType;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "location", //
		indexes = { //
				@Index(columnList = "name, type"), //
				@Index(columnList = "type") //
		})
public class LocationEntity //
		extends AbstractNamedCreatedKeyedEntity<Long> {

	private static final long serialVersionUID = -9066269489440543721L;

	@Column(nullable = false)
	private LocationType type;

	@Override
	public String toString() {
		return getName();
	}
}
