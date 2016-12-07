package ph.txtdis.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import ph.txtdis.dto.SalesItemVariance;

@Service("remittanceVarianceService")
public class RemittanceVarianceService extends AbstractSalesItemVarianceService {

	@Override
	public String getHeaderText() {
		return "Remittance Variance";
	}

	@Override
	public String getModule() {
		return "remittanceVariance";
	}

	@Override
	public String getActualHeader() {
		return "Load-in";
	}

	@Override
	public String getExpectedHeader() {
		return "Load-out";
	}

	@Override
	public String getVarianceHeader() {
		return "Net";
	}

	public BigDecimal getActualValue() {
		// TODO Auto-generated method stub
		list().stream().map(SalesItemVariance::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
		return null;
	}

	public BigDecimal getVarianceValue() {
		// TODO Auto-generated method stub
		return null;
	}
}
