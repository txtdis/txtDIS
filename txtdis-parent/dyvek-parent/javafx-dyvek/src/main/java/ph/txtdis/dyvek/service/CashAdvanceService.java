package ph.txtdis.dyvek.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dyvek.model.CashAdvance;
import ph.txtdis.info.Information;
import ph.txtdis.service.BankDrawnCheckService;
import ph.txtdis.service.ListedAndResetableService;
import ph.txtdis.service.TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService;
import ph.txtdis.type.PartnerType;

public interface CashAdvanceService //
		extends BankDrawnCheckService<CashAdvance>, ListedAndResetableService<CashAdvance>, TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService {

	static final String CASH_ADVANCE = "-CASH ADVANCE-";

	List<String> listCustomers();

	CashAdvance save() throws Information, Exception;

	void setBank(String bank);

	void setCashAdvances(PartnerType type, String customer) throws Exception;

	void setCheckDate(LocalDate d);

	void setCheckId(Long id);

	void setCustomer(String customer);

	void setTotalValue(BigDecimal amt);
}
