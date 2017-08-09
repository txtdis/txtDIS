package ph.txtdis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class Remittance
	extends AbstractDecisionNeeded<Long>
	implements DecisionNeededValidatedCreatedKeyed<Long> {

	private BigDecimal value;

	private List<RemittanceDetail> details;

	private LocalDate paymentDate, receivedDate;

	private Long checkId;

	private String depositorBank, draweeBank, depositor, receivedBy, receivedFrom, remarks;

	private ZonedDateTime depositedOn, depositorOn, receivedOn;

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
