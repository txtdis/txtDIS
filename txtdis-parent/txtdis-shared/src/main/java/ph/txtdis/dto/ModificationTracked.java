package ph.txtdis.dto;

import java.time.ZonedDateTime;

public interface ModificationTracked {

	String getDeactivatedBy();

	void setDeactivatedBy(String u);

	ZonedDateTime getDeactivatedOn();

	void setDeactivatedOn(ZonedDateTime zdt);

	String getLastModifiedBy();

	void setLastModifiedBy(String u);

	ZonedDateTime getLastModifiedOn();

	void setLastModifiedOn(ZonedDateTime zdt);
}
