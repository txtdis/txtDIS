package ph.txtdis.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreditNote //
		extends AbstractDecisionNeeded<Long> //
		implements DecisionNeededValidatedCreatedKeyed<Long> {

	private BigDecimal balanceValue, totalValue;

	private List<CreditNotePayment> payments;

	private LocalDate creditDate;

	private String description, reference, lastModifiedBy;

	private ZonedDateTime lastModifiedOn;
}
