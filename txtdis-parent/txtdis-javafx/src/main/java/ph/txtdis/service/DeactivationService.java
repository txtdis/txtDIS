package ph.txtdis.service;

import static java.time.ZonedDateTime.now;

import java.time.ZonedDateTime;

import ph.txtdis.dto.ModificationTracked;
import ph.txtdis.info.Information;

public interface DeactivationService<PK> //
	extends SavedService<PK> {

	default void deactivate() throws Information, Exception {
		((ModificationTracked) get()).setDeactivatedBy(getUsername());
		((ModificationTracked) get()).setDeactivatedOn(now());
		save();
	}

	String getUsername();

	default String getDeactivatedBy() {
		return ((ModificationTracked) get()).getDeactivatedBy();
	}

	default ZonedDateTime getDeactivatedOn() {
		return ((ModificationTracked) get()).getDeactivatedOn();
	}
}
