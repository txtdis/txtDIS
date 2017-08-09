package ph.txtdis.mgdc.gsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.CreditDetail;
import ph.txtdis.dto.CustomerCredit;
import ph.txtdis.exception.BadCreditException;
import ph.txtdis.exception.ExceededCreditLimitException;
import ph.txtdis.exception.NoAssignedCustomerSellerException;
import ph.txtdis.exception.NotTheAssignedCustomerSellerException;
import ph.txtdis.mgdc.gsm.dto.Customer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.type.UserType.SALES_ENCODER;
import static ph.txtdis.util.NumberUtils.isNegative;
import static ph.txtdis.util.NumberUtils.isPositive;
import static ph.txtdis.util.UserUtils.isUser;
import static ph.txtdis.util.UserUtils.username;

@Service("customerValidationService")
public class CustomerValidationServiceImpl //
	implements CustomerValidationService {

	@Autowired
	private GsmBillingService billingService;

	@Autowired
	private CustomerService customerService;

	private BigDecimal remainingCredit;

	private Customer customer;

	private LocalDate date;

	@Override
	public Customer validate(Long id, LocalDate d) throws Exception {
		date = d;
		confirmCustomerExistsAndNotDeactivated(id);
		if (!isUser(MANAGER))
			validate();
		return customer;
	}

	private void confirmCustomerExistsAndNotDeactivated(Long id) throws Exception {
		customer = customerService.findActive(id);
	}

	private void validate() throws Exception {
		confirmCurrentUserIsTheCustomerAssignedSeller();
		confirmCustomerHasNoOverdues();
		confirmCustomerHasNotExceededItsCreditLimit();
	}

	private void confirmCurrentUserIsTheCustomerAssignedSeller() throws Exception {
		if (isUser(SALES_ENCODER) && isNotAPreSellRoute())
			return;
		String seller = customerService.getSeller(customer, date);
		if (seller == null)
			throw new NoAssignedCustomerSellerException(customer.getName());
		if (!seller.equals(username()))
			throw new NotTheAssignedCustomerSellerException(seller, customer.getName());
	}

	private void confirmCustomerHasNoOverdues() throws Exception {
		BigDecimal overdue = sumUnpaid(billingService.listAged(customer));
		if (isPositive(overdue))
			throw new BadCreditException(customer.getName(), overdue);
	}

	private void confirmCustomerHasNotExceededItsCreditLimit() throws Exception {
		BigDecimal creditLimit = getCreditLimit();
		remainingCredit = creditLimit.subtract(getAgingValue());
		if (isNegative(remainingCredit))
			throw new ExceededCreditLimitException(customer.getName(), creditLimit, remainingCredit);
	}

	private boolean isNotAPreSellRoute() {
		return !customerService.areDeliveriesBooked(customer, date);
	}

	private BigDecimal sumUnpaid(List<Billable> list) {
		return list == null ? ZERO : list.stream().map(r -> r.getUnpaidValue()).reduce(ZERO, BigDecimal::add);
	}

	private BigDecimal getCreditLimit() {
		try {
			return getCreditDetail().getCreditLimit();
		} catch (Exception e) {
			return ZERO;
		}
	}

	private BigDecimal getAgingValue() {
		List<Billable> list = billingService.listAging(customer);
		return sumUnpaid(list);
	}

	private CreditDetail getCreditDetail() throws Exception {
		return customer.getCreditDetails().stream() //
			.filter(p -> isApprovedAndStartDateIsNotInTheFuture(p, date)) //
			.max(CustomerCredit::compareTo) //
			.get();
	}

	@Override
	public BigDecimal getRemainingCreditValue() {
		return remainingCredit;
	}

	@Override
	public long getTermsInDays() {
		try {
			return getCreditDetail().getTermInDays();
		} catch (Exception e) {
			return 0;
		}
	}
}
