package ph.txtdis.dto;

import static ph.txtdis.util.NumberUtils.zeroIfNull;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
