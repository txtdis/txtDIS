package ph.txtdis.service;

import java.time.LocalDate;

import ph.txtdis.dto.StartDated;
import ph.txtdis.dto.Validated;

public interface LatestApproved {

	default <T extends Validated> boolean isApprovedAndStartDateIsNotInTheFuture(T t, LocalDate d) {
		return t.getIsValid() != null && t.getIsValid() == true && ((StartDated) t).getStartDate().compareTo(d) <= 0;
	}
}
