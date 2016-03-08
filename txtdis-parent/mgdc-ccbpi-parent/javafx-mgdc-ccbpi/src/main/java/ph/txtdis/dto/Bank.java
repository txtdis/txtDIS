package ph.txtdis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Bank extends EntityCreationTracked<Long> {

	private String name;
}
