package ph.txtdis.dto;

import java.time.ZonedDateTime;

public interface EntityDecisionNeeded<PK> extends CreationTracked, Validated, Keyed<PK> {

	String getRemarks();

	void setRemarks(String s);

	void setIsValid(Boolean isValid);

	String getDecidedBy();

	void setDecidedBy(String username);

	ZonedDateTime getDecidedOn();

	void setDecidedOn(ZonedDateTime decidedOn);
}
