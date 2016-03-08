package ph.txtdis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Customer extends EntityCreationTracked<Long> {

	private Long code;

	private String name;
}
