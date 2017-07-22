package ph.txtdis.service;

import ph.txtdis.dto.Keyed;
import ph.txtdis.exception.DuplicateCheckException;

public interface BankDrawnCheckService<T extends Keyed<Long>> //
		extends FinancialService {

	default void verifyCheck(String bank, Long checkId) throws Exception {
		T t = findByCheck(bank, checkId);
		if (t != null)
			throw new DuplicateCheckException(bank, checkId, t.getId());
	}

	T findByCheck(String bank, Long checkId) throws Exception;
}