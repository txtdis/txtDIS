package ph.txtdis.dto;

import java.time.ZonedDateTime;

public interface CreationLogged {

	String getCreatedBy();

	ZonedDateTime getCreatedOn();
}
