package ph.txtdis.dto;

import lombok.Data;

@Data
public abstract class AbstractKeyed<PK> //
	implements Keyed<PK> {

	private PK id;
}