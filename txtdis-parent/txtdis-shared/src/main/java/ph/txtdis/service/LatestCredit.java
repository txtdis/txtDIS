package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.CustomerCredit;

public interface LatestCredit
	extends LatestApproved {

	default <T extends CustomerCredit> T getCredit(List<T> list, LocalDate date) {
		try {
			return list.stream()//
				.filter(p -> isApprovedAndStartDateIsNotInTheFuture(p, date))//
				.max(CustomerCredit::compareTo) //
				.get();
		} catch (Exception e) {
			return null;
		}
	}

	default BigDecimal getCreditLimit() {
		try {
			return getCredit().getCreditLimit();
		} catch (Exception e) {
			return ZERO;
		}
	}

	<T extends CustomerCredit> T getCredit();

	default int getCreditTerm() {
		try {
			return getCredit().getTermInDays();
		} catch (Exception e) {
			return 0;
		}
	}
}
