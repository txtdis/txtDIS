package ph.txtdis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.ItemTier;
import ph.txtdis.type.UomType;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemFamily extends AbstractCreationTracked<Long> implements Comparable<ItemFamily>, Named {

	private String name;

	private ItemTier tier;

	private UomType uom;

	@Override
	public int compareTo(ItemFamily f) {
		return getTier().compareTo(f.getTier());
	}

	@Override
	public String toString() {
		return name;
	}
}
