package ph.txtdis.dto;

import java.time.ZonedDateTime;

public interface DecisionNeededValidatedCreatedKeyed<PK> //
	extends CreationLogged,
	Validated,
	Remarked,
	Keyed<PK> {

	void setIsValid(Boolean isValid);

	String getDecidedBy();

	void setDecidedBy(String username);

	ZonedDateTime getDecidedOn();

	void setDecidedOn(ZonedDateTime decidedOn);
}
