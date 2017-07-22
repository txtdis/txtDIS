package ph.txtdis.dto;

import java.time.ZonedDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractDecisionNeeded<PK> //
		extends AbstractCreationTracked<PK> {

	private Boolean isValid;

	private String decidedBy, remarks;

	private ZonedDateTime decidedOn;
}