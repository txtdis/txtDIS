package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ph.txtdis.type.LocationType;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "location", //
		indexes = { //
				@Index(columnList = "name, type"), //
				@Index(columnList = "type") //
		})
public class LocationEntity extends AbstractNamedEntity<Long> {

	private static final long serialVersionUID = -9066269489440543721L;

	public LocationEntity(String name, LocationType type) {
		this.name = name;
		this.type = type;
	}

	@Column(nullable = false)
	private LocationType type;

	@Override
	public String toString() {
		return name;
	}
}
