package ph.txtdis.dto;

import static ph.txtdis.type.BillableType.INVOICE;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import ph.txtdis.type.BillableType;

@Getter
@Setter
public class Vat implements Keyed<Long>, Typed {

	private Long id, nbrId;

	private String prefix, suffix, customer;

	private LocalDate orderDate;

	private BigDecimal value, vatValue;

	@Override
	public BillableType type() {
		return INVOICE;
	}
}
