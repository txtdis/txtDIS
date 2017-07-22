package ph.txtdis.mgdc.gsm.dto;

import static ph.txtdis.type.ModuleType.INVOICE;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import ph.txtdis.dto.Keyed;
import ph.txtdis.dto.Typed;
import ph.txtdis.type.ModuleType;

@Data
public class Vat //
		implements Keyed<Long>, Typed {

	private Long id, nbrId;

	private String prefix, suffix, customer;

	private LocalDate orderDate;

	private BigDecimal value, vatableValue, vatValue;

	@Override
	public ModuleType type() {
		return INVOICE;
	}
}
