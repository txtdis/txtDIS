package ph.txtdis.dyvek.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.AbstractCreationTracked;
import ph.txtdis.dto.Named;

@Data
@EqualsAndHashCode(callSuper = true)
public class Item //
		extends AbstractCreationTracked<Long> //
		implements Named {

	private String name;

	@Override
	public String toString() {
		return name;
	}
}
