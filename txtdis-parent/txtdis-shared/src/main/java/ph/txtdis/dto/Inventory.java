package ph.txtdis.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Inventory //
	implements Keyed<Long> {

	private Long id;

	private String item;

	private Integer daysLevel;

	private BigDecimal avgDailySoldQty, goodQty, badQty, value, obsolesenceValue;
}
