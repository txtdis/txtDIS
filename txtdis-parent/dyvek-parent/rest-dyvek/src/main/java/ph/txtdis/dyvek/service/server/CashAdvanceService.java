package ph.txtdis.dyvek.service.server;

import ph.txtdis.dyvek.domain.CashAdvanceEntity;
import ph.txtdis.dyvek.domain.RemittanceEntity;
import ph.txtdis.dyvek.model.CashAdvance;
import ph.txtdis.service.SavedKeyedService;
import ph.txtdis.type.PartnerType;

import java.util.List;

public interface CashAdvanceService
	extends SavedKeyedService<CashAdvanceEntity, CashAdvance, Long> {

	String CASH_ADVANCE = "-CASH ADVANCE-";

	CashAdvance findByCheck(String bank, Long id);

	List<CashAdvance> findByCustomer(PartnerType type, String name);

	List<CashAdvance> findByOrderByBalanceValueDescIssuedDateDesc();

	void update(RemittanceEntity e);
}
