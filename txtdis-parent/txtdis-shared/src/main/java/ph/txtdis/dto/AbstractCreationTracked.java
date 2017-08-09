package ph.txtdis.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public abstract class AbstractCreationTracked<PK> //
	implements CreationLogged,
	Keyed<PK> {

	private PK id;

	private String createdBy;

	private ZonedDateTime createdOn;
}