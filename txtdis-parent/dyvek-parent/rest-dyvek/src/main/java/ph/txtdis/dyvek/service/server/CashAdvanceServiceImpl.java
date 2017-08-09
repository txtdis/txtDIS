package ph.txtdis.dyvek.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.dyvek.domain.CashAdvanceEntity;
import ph.txtdis.dyvek.domain.CustomerEntity;
import ph.txtdis.dyvek.domain.RemittanceEntity;
import ph.txtdis.dyvek.model.CashAdvance;
import ph.txtdis.dyvek.repository.CashAdvanceRepository;
import ph.txtdis.service.AbstractSavedKeyedService;
import ph.txtdis.type.PartnerType;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;

@Lazy
@Service("cashAdvanceService")
public class CashAdvanceServiceImpl 
	extends AbstractSavedKeyedService<CashAdvanceRepository, CashAdvanceEntity, CashAdvance, Long> 
	implements CashAdvanceService {

	private final CustomerService customerService;

	public CashAdvanceServiceImpl(CustomerService customerService) {
		this.customerService = customerService;
	}

	@Override
	public CashAdvance findByCheck(String bank, Long id) {
		CashAdvanceEntity e = repository.findByBankNameAndCheckId(bank, id);
		return toModel(e);
	}

	@Override
	protected CashAdvance toModel(CashAdvanceEntity e) {
		if (e == null)
			return null;
		CashAdvance t = new CashAdvance();
		t.setId(e.getId());
		t.setCustomer(customer(e));
		t.setBank(bank(e));
		t.setCheckDate(e.getCheckDate());
		t.setCheckId(e.getCheckId());
		t.setTotalValue(e.getTotalValue());
		t.setBalanceValue(e.getBalanceValue());
		return t;
	}

	private String customer(CashAdvanceEntity e) {
		return customer(e.getCustomer());
	}

	private String bank(CashAdvanceEntity e) {
		return customer(e.getBank());
	}

	private String customer(CustomerEntity c) {
		return c == null ? null : c.getName();
	}

	@Override
	public List<CashAdvance> findByCustomer(PartnerType type, String name) {
		List<CashAdvanceEntity> l =
			repository.findByCustomerTypeAndCustomerNameAndBalanceValueGreaterThanOrderByCheckDateDesc(type, name, ZERO);
		return toModels(l);
	}

	@Override
	public List<CashAdvance> findByOrderByBalanceValueDescIssuedDateDesc() {
		List<CashAdvanceEntity> l = repository.findByOrderByBalanceValueDescCheckDateDesc();
		return toModels(l);
	}

	@Override
	protected CashAdvanceEntity toEntity(CashAdvance t) {
		return t == null ? null : create(t);
	}

	private CashAdvanceEntity create(CashAdvance t) {
		CashAdvanceEntity e = new CashAdvanceEntity();
		e.setCustomer(customer(t));
		e.setBank(bank(t));
		e.setCheckDate(t.getCheckDate());
		e.setCheckId(t.getCheckId());
		e.setTotalValue(t.getTotalValue());
		e.setBalanceValue(t.getBalanceValue());
		return e;
	}

	private CustomerEntity customer(CashAdvance t) {
		return customer(t.getCustomer());
	}

	private CustomerEntity bank(CashAdvance t) {
		return customer(t.getBank());
	}

	private CustomerEntity customer(String name) {
		return customerService.findEntityByName(name);
	}

	@Override
	public void update(RemittanceEntity r) {
		CashAdvanceEntity e = repository.findOne(r.getCheckId());
		e.setBalanceValue(balance(e, r));
		post(e);
	}

	private BigDecimal balance(CashAdvanceEntity e, RemittanceEntity r) {
		BigDecimal balance = e.getBalanceValue();
		BigDecimal payment = r.getValue();
		return balance.subtract(payment);
	}
}