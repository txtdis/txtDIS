package ph.txtdis.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Bom //
		extends AbstractCreationTracked<Long> //
		implements Keyed<Long> {

	private String part;

	private BigDecimal qty;

	@Override
	public String toString() {
		return getPart() + ": " + getQty();
	}
}