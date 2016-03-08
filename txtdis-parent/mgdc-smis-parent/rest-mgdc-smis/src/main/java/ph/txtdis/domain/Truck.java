package ph.txtdis.domain;

import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Truck extends Named<Long> {

	private static final long serialVersionUID = -8355723680264208431L;

	@Override
	public String toString() {
		return name;
	}
}
