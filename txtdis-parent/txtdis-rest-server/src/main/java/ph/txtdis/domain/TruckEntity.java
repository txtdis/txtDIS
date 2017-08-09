package ph.txtdis.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.Named;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "truck", //
	uniqueConstraints = @UniqueConstraint(name = "truck_name_key", columnNames = {"name"}))
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
