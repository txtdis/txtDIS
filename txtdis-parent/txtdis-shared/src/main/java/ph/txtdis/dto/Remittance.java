package ph.txtdis.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Remittance //
		extends AbstractDecisionNeeded<Long> //
		implements DecisionNeededValidatedCreatedKeyed<Long> {

	private BigDecimal value;

	private List<RemittanceDetail> details;

	private LocalDate paymentDate, receivedDate;

	private Long checkId;

	private String depositorBank, draweeBank, depositor, receivedBy, remarks, collector;

	private ZonedDateTime receivedOn, depositedOn, depositorOn;

	public List<RemittanceDetail> getDetails() {
		return details == null ? Collections.emptyList() : details;
	}

	public String getDraweeBank() {
		return draweeBank == null ? "CASH" : draweeBank;
	}

	@Override
	public String toString() {
		return "Remit No. " + getId();
	}
}
