package ph.txtdis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractModificationTracked<PK> //
	extends AbstractCreationTracked<PK> //
	implements ModificationTracked {

	private String deactivatedBy, lastModifiedBy;

	private ZonedDateTime deactivatedOn, lastModifiedOn;
}