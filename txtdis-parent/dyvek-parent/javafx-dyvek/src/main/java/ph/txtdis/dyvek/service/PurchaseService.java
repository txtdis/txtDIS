package ph.txtdis.dyvek.service;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dyvek.model.Billable;

public interface PurchaseService //
		extends OrderService {

	List<Billable> findExpiringTheFollowingDayOrHaveExpired();

	LocalDate getEndDate();

	void setEndDateUponValidation(LocalDate end) throws Exception;
}
