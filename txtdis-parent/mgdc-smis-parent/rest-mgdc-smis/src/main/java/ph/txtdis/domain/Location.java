package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.LocationType;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(indexes = { @Index(columnList = "type, name") })
public class Location extends Named<Long> {

	private static final long serialVersionUID = -9066269489440543721L;

	@Column(nullable = false)
	private LocationType type;

	@Override
	public String toString() {
		return name;
	}
}
