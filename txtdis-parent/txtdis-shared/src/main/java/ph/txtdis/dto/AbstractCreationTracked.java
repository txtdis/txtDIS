package ph.txtdis.dto;

import java.time.ZonedDateTime;

import lombok.Data;

@Data
public abstract class AbstractCreationTracked<PK> implements CreationTracked, Keyed<PK> {

	private PK id;

	private String createdBy;

	private ZonedDateTime createdOn;
}