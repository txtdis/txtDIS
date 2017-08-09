package ph.txtdis.dyvek.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dyvek.model.CashAdvance;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.info.Information;
import ph.txtdis.service.RemittanceService;
import ph.txtdis.service.RestClientService;
import ph.txtdis.type.PartnerType;
import ph.txtdis.util.ClientTypeMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service("cashAdvanceService")
public class CashAdvanceServiceImpl //
	implements CashAdvanceService {

	@Autowired
	private VendorService customerService;

	@Autowired
	private RemittanceService remitService;

	@Autowired
	private RestClientService<CashAdvance> restClientService;

	@Autowired
	private ClientTypeMap typeMap;

	private CashAdvance cashAdvance;

	private List<CashAdvance> cashAdvances;

	@Override
	public void verifyCheck(String bank, Long checkId) throws Exception {
		remitService.verifyCheck(bank, checkId);
		CashAdvanceService.super.verifyCheck(bank, checkId);
	}

	@Override
	public CashAdvance findByCheck(String bank, Long checkId) throws Exception {
		return findCashAdvance().getOne("/check?bank=" + bank + "&id=" + checkId);
	}

	private RestClientService<CashAdvance> findCashAdvance() {
		return restClientService.module(getModuleName());
	}

	@Override
	public String getModuleName() {
		return "cashAdvance";
	}

	@Override
	public RestClientService<CashAdvance> getRestClientServiceForLists() {
		return restClientService;
	}

	@Override
	public String getTitleName() {
		return getHeaderName();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public List<String> listBanks() {
		return remitService.listBanks();
	}

	@Override
	public List<String> listCustomers() {
		return customerService.listNames("tradingPartners");
	}

	@Override
	public void reset() {
		set(null);
		setList(null);
	}

	private CashAdvance set(CashAdvance cashAdvance) {
		return this.cashAdvance = cashAdvance;
	}

	private void setList(List<CashAdvance> l) {
		cashAdvances = l;
	}

	@Override
	public CashAdvance save() throws Information, Exception {
		return set(restClientService.module(getModuleName()).save(get()));
	}

	private CashAdvance get() {
		if (cashAdvance == null)
			set(new CashAdvance());
		return cashAdvance;
	}

	@Override
	public void setBank(String name) {
		get().setBank(name);
	}

	@Override
	public void setCheckId(Long id) {
		get().setCheckId(id);
	}

	@Override
	public void setCheckDate(LocalDate d) {
		get().setCheckDate(d);
	}

	@Override
	public void setCustomer(String name) {
		get().setCustomer(name);
	}

	@Override
	public void setTotalValue(BigDecimal amt) {
		get().setTotalValue(amt);
		get().setBalanceValue(amt);
	}

	@Override
	public void setCashAdvances(PartnerType type, String customer) throws Exception {
		setList(findCashAdvance().getList("/customer?type=" + type + "&name=" + customer));
		if (list() == null || list().isEmpty())
			throw new NotFoundException("Cash advance for\n" + customer);
	}

	@Override
	public List<CashAdvance> list() {
		return cashAdvances;
	}
}
