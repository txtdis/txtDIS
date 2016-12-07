package ph.txtdis.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Remittance extends AbstractDecisionNeeded<Long> implements EntityDecisionNeeded<Long> {

	private BigDecimal value;

	private List<RemittanceDetail> details;

	private LocalDate paymentDate;

	private Long checkId;

	private String depositorBank, draweeBank, depositor, receivedBy, remarks, collector;

	private ZonedDateTime receivedOn, depositedOn, depositorOn;

	public boolean isDeposited() {
		return depositedOn != null;
	}

	@Override
	public String toString() {
		return "Collection ID " + getId();
	}
}
