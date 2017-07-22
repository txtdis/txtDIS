package ph.txtdis.mgdc.ccbpi.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import ph.txtdis.dto.Remittance;
import ph.txtdis.service.AbstractRemittanceService;

@Service("remittanceService")
public class RemittanceServiceImpl //
		extends AbstractRemittanceService //
		implements CokeRemittanceService {

	@Override
	public BigDecimal getTotalValue(String collector, LocalDate start, LocalDate end) {
		try {
			List<Remittance> l = listRemittances("/listPerCollector?name=" + collector + "&start=" + start + "&end=" + end);
			return l.stream().map(Remittance::getValue).reduce(BigDecimal::add).get();
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}
}
