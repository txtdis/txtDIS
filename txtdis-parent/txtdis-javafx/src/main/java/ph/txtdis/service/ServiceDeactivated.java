package ph.txtdis.service;

import static java.time.ZonedDateTime.now;

import java.time.ZonedDateTime;

import ph.txtdis.dto.ModificationTracked;
import ph.txtdis.info.Information;

public interface ServiceDeactivated<PK> extends Saved<PK> {

	default void deactivate() throws Information, Exception {
		((ModificationTracked) get()).setDeactivatedBy(username());
		((ModificationTracked) get()).setDeactivatedOn(now());
		save();
	}

	default String getDeactivatedBy() {
		return ((ModificationTracked) get()).getDeactivatedBy();
	}

	default ZonedDateTime getDeactivatedOn() {
		return ((ModificationTracked) get()).getDeactivatedOn();
	}

	String username();
}
