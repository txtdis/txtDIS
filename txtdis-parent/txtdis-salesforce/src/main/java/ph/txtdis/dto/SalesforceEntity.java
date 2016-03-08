package ph.txtdis.dto;

import java.time.ZonedDateTime;

public interface SalesforceEntity {

	String getIdNo();

	ZonedDateTime getUploadedOn();

	void setUploadedOn(ZonedDateTime zdt);
}
