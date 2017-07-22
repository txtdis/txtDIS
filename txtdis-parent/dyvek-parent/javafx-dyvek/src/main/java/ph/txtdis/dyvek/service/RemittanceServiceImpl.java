package ph.txtdis.dyvek.service;

import static java.util.Arrays.asList;
import static ph.txtdis.dyvek.service.CashAdvanceService.CASH_ADVANCE;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Remittance;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.service.AbstractPaymentDetailedRemittanceService;
import ph.txtdis.type.PartnerType;

@Service("remittanceService")
public class RemittanceServiceImpl //
		extends AbstractPaymentDetailedRemittanceService //
		implements VendorAndClientCheckPaymentDetailedRemittanceService {

	@Autowired
	private CashAdvanceService cashAdvanceService;

	@Autowired
	private DeliveryService deliveryService;

	@Autowired
	private VendorBillingService vendorBillingService;

	@Override
	public Remittance createEntity() {
		get().setCollector(vendor());
		get().setValue(netValue());
		get().setDetails(details());
		return get();
	}

	private String vendor() {
		return vendorBillingService.getCustomer();
	}

	private BigDecimal netValue() {
		return vendorBillingService.getNetValue();
	}

	private List<RemittanceDetail> details() {
		RemittanceDetail d = new RemittanceDetail();
		d.setId(vendorBillingService.getId());
		d.setPaymentValue(netValue());
		return asList(d);
	}

	private List<RemittanceDetail> findUnpaidBillings() {
		List<Billable> l = deliveryService.findUnpaidBillings(customer());
		return l == null ? null //
				: l.stream().map(b -> toDetail(b)).collect(Collectors.toList());
	}

	private String customer() {
		return get().getCollector();
	}

	private RemittanceDetail toDetail(Billable b) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RemittanceDetail> getDetails() {
		return !isNew() ? super.getDetails() : findUnpaidBillings();
	}

	@Override
	public boolean isAppendable() {
		return false;
	}

	@Override
	public void verifyCashAdvance(PartnerType type) throws Exception {
		cashAdvanceService.setCashAdvances(type, vendor());
	}

	@Override
	public void verifyCheck(String bank, Long checkId) throws Exception {
		if (!bank.equals(CASH_ADVANCE))
			super.verifyCheck(bank, checkId);
	}
}
