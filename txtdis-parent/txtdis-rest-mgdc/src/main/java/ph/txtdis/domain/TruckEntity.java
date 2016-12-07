package ph.txtdis.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.Named;

@Data
@Entity
@Table(name = "truck")
@EqualsAndHashCode(callSuper = true)
public class TruckEntity extends AbstractNamedEntity<Long> implements Named {

	private static final long serialVersionUID = -8355723680264208431L;

	@Override
	public String toString() {
		return name;
	}
}
