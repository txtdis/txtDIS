package ph.txtdis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Truck //
		extends AbstractCreationTracked<Long> //
		implements Named {

	private String name, route;

	@Override
	public String toString() {
		return getName();
	}
}
