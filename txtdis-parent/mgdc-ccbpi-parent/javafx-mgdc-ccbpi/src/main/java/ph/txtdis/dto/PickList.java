package ph.txtdis.dto;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PickList extends EntityCreationTracked<Long> {

	private String printedBy, remarks;

	private LocalDate pickDate;

	private ZonedDateTime printedOn;

	private List<Item> item;

	@Override
	public String toString() {
		return "Pick List No. " + getId();
	}
}
