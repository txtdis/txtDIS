package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.util.NumberUtils.isNegative;
import static ph.txtdis.util.NumberUtils.isPositive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.CreditDetail;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.CustomerCredit;
import ph.txtdis.exception.BadCreditException;
import ph.txtdis.exception.DeactivatedException;
import ph.txtdis.exception.ExceededCreditLimitException;
import ph.txtdis.exception.NoAssignedCustomerSellerException;
import ph.txtdis.exception.NotTheAssignedCustomerSellerException;
import ph.txtdis.type.UserType;

@Service("customerValidationService")
public class CustomerValidationServiceImpl implements CustomerValidationService {

	@Autowired
	private BillingService billingService;

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private CustomerService customerService;

	private BigDecimal remainingCredit;

	private Customer customer;

	private LocalDate date;

	@Override
	public Customer validate(Long id, LocalDate d) throws Exception {
		date = d;
		confirmCustomerExistsAndNotDeactivated(id);
		if (!credentialService.isUser(MANAGER))
			validate();
		return customer;
	}

	private void validate() throws Exception {
		confirmCurrentUserIsTheCustomerAssignedSeller();
		confirmCustomerHasNoOverdues();
		confirmCustomerHasNotExceededItsCreditLimit();
	}

	private void confirmCustomerExistsAndNotDeactivated(Long id) throws Exception {
		Customer c = customerService.find(id);
		if (c.getDeactivatedOn() != null)
			throw new DeactivatedException(c.getName());
		customer = c;
	}

	private void confirmCurrentUserIsTheCustomerAssignedSeller() throws Exception {
		if (credentialService.isUser(UserType.SALES_ENCODER) && isNotAPreSellRoute())
			return;
		String seller = customer.getSeller(date);
		if (seller == null)
			throw new NoAssignedCustomerSellerException(customer.getName());
		if (!seller.equals(credentialService.username()))
			throw new NotTheAssignedCustomerSellerException(seller, customer.getName());
	}

	private boolean isNotAPreSellRoute() {
		return !customer.areDeliveriesBooked(date);
	}

	private void confirmCustomerHasNoOverdues() throws Exception {
		BigDecimal o = getAgedValue();
		if (isPositive(o))
			throw new BadCreditException(customer, o);
	}

	private BigDecimal getAgedValue() {
		return sumUnpaid(billingService.listAged(customer));
	}

	private BigDecimal sumUnpaid(List<Billable> list) {
		return list.stream().map(r -> r.getUnpaidValue()).reduce(ZERO, BigDecimal::add);
	}

	private void confirmCustomerHasNotExceededItsCreditLimit() throws Exception {
		BigDecimal creditLimit = getCreditLimit();
		remainingCredit = creditLimit.subtract(getAgingValue());
		if (isNegative(remainingCredit))
			throw new ExceededCreditLimitException(customer, creditLimit, remainingCredit);
	}

	private BigDecimal getCreditLimit() {
		try {
			return getCreditDetail().getCreditLimit();
		} catch (Exception e) {
			return ZERO;
		}
	}

	private CreditDetail getCreditDetail() throws Exception {
		return customer.getCreditDetails().stream() //
				.filter(p -> isApprovedAndStartDateIsNotInTheFuture(p, date)) //
				.max(CustomerCredit::compareTo) //
				.get();
	}

	private BigDecimal getAgingValue() {
		List<Billable> list = billingService.listAging(customer);
		return sumUnpaid(list);
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
