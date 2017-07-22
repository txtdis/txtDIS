package ph.txtdis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.ItemTier;
import ph.txtdis.type.UomType;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemFamily //
		extends AbstractCreationTracked<Long> //
		implements Named {

	private String name;

	private ItemTier tier;

	private UomType uom;

	@Override
	public String toString() {
		return getName();
	}
}
