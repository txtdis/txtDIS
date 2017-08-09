package ph.txtdis.dyvek.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.AbstractCreationTracked;
import ph.txtdis.dto.Named;
import ph.txtdis.type.PartnerType;

@Data
@EqualsAndHashCode(callSuper = true)
public class Customer //
	extends AbstractCreationTracked<Long> //
	implements Named {

	private String name;

	private PartnerType type;

	@Override
	public String toString() {
		return name;
	}
}
