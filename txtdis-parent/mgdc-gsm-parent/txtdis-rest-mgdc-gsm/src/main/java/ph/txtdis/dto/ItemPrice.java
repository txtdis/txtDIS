package ph.txtdis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

import static ph.txtdis.util.NumberUtils.zeroIfNull;

@Data
@EqualsAndHashCode
public class ItemPrice {

	private long id;

	private String name;

	private String description;

	private BigDecimal value;

	public BigDecimal getValue() {
		return zeroIfNull(value);
	}
}
