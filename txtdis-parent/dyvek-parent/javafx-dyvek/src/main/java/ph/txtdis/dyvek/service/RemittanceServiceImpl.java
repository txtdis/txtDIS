package ph.txtdis.dyvek.service;

import org.springframework.stereotype.Service;
import ph.txtdis.dto.Remittance;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.service.AbstractPaymentDetailedRemittanceService;
import ph.txtdis.type.PartnerType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.dyvek.service.CashAdvanceService.CASH_ADVANCE;

@Service("remittanceService")
public class RemittanceServiceImpl
	extends AbstractPaymentDetailedRemittanceService
	implements VendorAndClientCheckPaymentDetailedRemittanceService {

	private final CashAdvanceService cashAdvanceService;

	private final DeliveryService deliveryService;

	private final TradingClientService clientService;

	private final VendorBillingService vendorBillingService;

	private List<String> clients;

	public RemittanceServiceImpl(CashAdvanceService cashAdvanceService,
	                             DeliveryService deliveryService,
	                             TradingClientService clientService,
	                             VendorBillingService vendorBillingService) {
		this.cashAdvanceService = cashAdvanceService;
		this.deliveryService = deliveryService;
		this.clientService = clientService;
		this.vendorBillingService = vendorBillingService;
	}

	@Override
	public Remittance createEntity() {
		get().setReceivedFrom(vendor());
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
		return singletonList(d);
	}

	@Override
	public LocalDate getCheckDate() {
		return get().getPaymentDate();
	}

	@Override
	public List<RemittanceDetail> getDetails() {
		return isNew() ? findUnpaidBillings() : super.getDetails() ;
	}

	private List<RemittanceDetail> findUnpaidBillings() {
		List<Billable> l = deliveryService.findUnpaidBillings(customer());
		return l == null ? null
			: l.stream().map(this::toDetail).collect(toList());
	}

	private String customer() {
		return get().getReceivedFrom();
	}

	private RemittanceDetail toDetail(Billable b) {
		return null;
	}

	@Override
	public List<String> getReceivedFromList() {
		if (clients == null)
			clients = clientService.listClients();
		return getId() == null ? clients : singletonList(get().getReceivedFrom());
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
