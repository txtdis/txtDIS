package ph.txtdis.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Item extends EntityCreationTracked<Long> {

	private long code;

	private String name;

	private Integer bottlePerCase;

	private BigDecimal priceValue;
}
