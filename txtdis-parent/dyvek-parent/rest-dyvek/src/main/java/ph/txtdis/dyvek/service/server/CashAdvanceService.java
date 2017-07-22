package ph.txtdis.dyvek.service.server;

import java.util.List;

import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.dyvek.domain.CashAdvanceEntity;
import ph.txtdis.dyvek.domain.RemittanceEntity;
import ph.txtdis.dyvek.model.CashAdvance;
import ph.txtdis.service.SavedKeyedService;
import ph.txtdis.type.PartnerType;

public interface CashAdvanceService //
		extends SavedKeyedService<CashAdvanceEntity, CashAdvance, Long> {

	static final String CASH_ADVANCE = "-CASH ADVANCE-";

	CashAdvance findByCheck(String bank, Long id);

	List<CashAdvance> findByCustomer(PartnerType type, String name);

	List<CashAdvance> findByOrderByBalanceValueDescIssuedDateDesc();

	List<RemittanceDetail> findLiquidationsByCashAdvanceId(Long id);

	void update(RemittanceEntity e);
}
