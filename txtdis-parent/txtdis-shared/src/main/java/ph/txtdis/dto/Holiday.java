package ph.txtdis.dto;

import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Holiday extends AbstractCreationTracked<Long> implements Named {

	private LocalDate declaredDate;

	private String name;

	@Override
	public String toString() {
		return toDateDisplay(declaredDate) + " - " + name;
	}
}
