package ph.txtdis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractDecisionNeeded<PK> //
	extends AbstractCreationTracked<PK> {

	private Boolean isValid;

	private String decidedBy, remarks;

	private ZonedDateTime decidedOn;
}