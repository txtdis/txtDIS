package ph.txtdis.mgdc.ccbpi.dto;

import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.AbstractCreationTracked;
import ph.txtdis.dto.Named;
import ph.txtdis.dto.StartDated;

@Data
@EqualsAndHashCode(callSuper = true)
public class Channel //
		extends AbstractCreationTracked<Long> //
		implements Named, StartDated {

	private String name;

	private LocalDate startDate;

	@Override
	public String toString() {
		return name;
	}
}
