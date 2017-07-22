package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.Named;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "truck", //
		uniqueConstraints = @UniqueConstraint(name = "truck_name_key", columnNames = { "name" }))
public class TruckEntity //
		extends AbstractCreatedKeyedEntity<Long> //
		implements Named {

	private static final long serialVersionUID = -8355723680264208431L;

	@Column(nullable = false)
	private String name;

	@Override
	public String toString() {
		return getName();
	}
}
