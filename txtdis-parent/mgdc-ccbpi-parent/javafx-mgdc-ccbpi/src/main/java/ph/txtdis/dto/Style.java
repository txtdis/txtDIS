package ph.txtdis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Style extends AbstractId<Long> {

	private String base, font;
}
