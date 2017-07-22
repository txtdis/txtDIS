package ph.txtdis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Style //
		extends AbstractCreationTracked<Long> //
		implements Keyed<Long> {

	private String base, font;
}
