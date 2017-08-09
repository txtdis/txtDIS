package ph.txtdis.dyvek.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.AbstractKeyed;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class Aging //
	extends AbstractKeyed<Long> {

	private String customer;

	private Long daysOver;

	private BigDecimal value;
}
