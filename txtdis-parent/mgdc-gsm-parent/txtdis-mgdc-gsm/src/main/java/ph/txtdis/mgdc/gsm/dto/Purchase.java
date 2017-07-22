package ph.txtdis.mgdc.gsm.dto;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.AbstractCreationTracked;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.User;

@Data
@EqualsAndHashCode(callSuper = true)
public class Purchase //
		extends AbstractCreationTracked<Long> {

	private Customer customer;

	private LocalDate orderDate;

	private String remarks;

	private List<BillableDetail> details;

	private User deactivatedBy;

	private ZonedDateTime deactivatedOn;

	private User mailedBy;

	private ZonedDateTime mailedOn;

	private User sentBy;

	private ZonedDateTime sentOn;

	private User receivedBy;

	private ZonedDateTime receivedOn;

	@Override
	public String toString() {
		return "P/O No. " + getId();
	}
}
