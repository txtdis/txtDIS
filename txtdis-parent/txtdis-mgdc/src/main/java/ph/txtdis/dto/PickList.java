package ph.txtdis.dto;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PickList //
		extends AbstractCreationTracked<Long> {

	private String truck, driver, assistant, leadAssistant, remarks, printedBy, receivedBy;

	private LocalDate pickDate;

	private List<Booking> bookings;

	private List<PickListDetail> details;

	private ZonedDateTime printedOn, receivedOn;

	@Override
	public String toString() {
		return "PickList No. " + getId();
	}
}
