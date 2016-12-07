package ph.txtdis.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreditNotePayment extends AbstractId<Long> implements Keyed<Long> {

	private BigDecimal paymentValue;

	private LocalDate paymentDate;

	private String reference, paymentRemarks;
}
