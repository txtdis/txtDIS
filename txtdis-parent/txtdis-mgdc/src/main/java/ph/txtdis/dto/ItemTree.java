package ph.txtdis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemTree //
		extends AbstractCreationTracked<Long> {

	private ItemFamily family, parent;
}
