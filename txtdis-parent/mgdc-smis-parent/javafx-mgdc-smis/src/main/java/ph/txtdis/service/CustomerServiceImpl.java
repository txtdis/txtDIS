package ph.txtdis.service;

import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Customer;
import ph.txtdis.dto.CustomerDiscount;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.SalesforceAccount;
import ph.txtdis.dto.SalesforceEntity;
import ph.txtdis.exception.DateInThePastException;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.salesforce.AccountUploader;
import ph.txtdis.util.Util;

@Service("customerService")
public class CustomerServiceImpl extends AbstractCustomerService
		implements PercentBasedCustomerDiscountService, SalesforceUploadService<SalesforceAccount> {

	@Autowired
	private AccountUploader accountUploader;

	@Autowired
	private ReadOnlyService<SalesforceAccount> salesforceService;

	@Override
	public String getUploadedBy() {
		return get().getUploadedBy();
	}

	@Override
	public ZonedDateTime getUploadedOn() {
		return get().getUploadedOn();
	}

	@Override
	public List<SalesforceAccount> forUpload() throws Exception {
		return salesforceService.module("salesforceAccount").getList();
	}

	@Override
	public void upload() throws Exception {
		List<SalesforceAccount> l = forUpload();
		if (l == null || l.isEmpty())
			throw new InvalidException("Nothing to upload");
		saveUploadedData(accountUploader.accounts(l).start());
	}

	@Override
	public void saveUploadedData(List<? extends SalesforceEntity> list) throws Exception {
		for (SalesforceEntity entity : list)
			saveCustomer(entity);
	}

	private void saveCustomer(SalesforceEntity entity) throws Exception {
		Customer b = find(entity.getIdNo());
		b.setUploadedBy(username());
		b.setUploadedOn(entity.getUploadedOn());
		save(b);
	}

	@Override
	public CustomerDiscount createDiscountUponValidation(int level, BigDecimal percent, ItemFamily family,
			LocalDate startDate) throws DateInThePastException, DuplicateException {
		validateFamilyAndStartDate(customerDiscounts(), startDate, level, family);
		return createCustomerDiscount(level, percent, nullIfAll(family), startDate);
	}

	private void validateFamilyAndStartDate(List<CustomerDiscount> list, LocalDate startDate, int level,
			ItemFamily family) throws DateInThePastException, DuplicateException {
		confirmDateIsNotInThePast(startDate);
		confirmFamilyAndStartDateAreUnique(list, startDate, level, family);
	}

	private void confirmFamilyAndStartDateAreUnique(List<CustomerDiscount> list, LocalDate startDate, int level,
			ItemFamily family) throws DuplicateException {
		if (list.stream().anyMatch(exist(startDate, level, family)))
			throw new DuplicateException(//
					"Discount level " + level + " for " + family + " of start date " + toDateDisplay(startDate));
	}

	private Predicate<? super CustomerDiscount> exist(LocalDate startDate, int level, ItemFamily f) {
		return r -> Util.areEqual(r.getStartDate(), startDate) //
				&& level == r.getLevel()//
				&& Util.areEqual(r.getFamilyLimit(), f);
	}

	private CustomerDiscount createCustomerDiscount(int level, BigDecimal percent, ItemFamily family,
			LocalDate startDate) {
		CustomerDiscount d = new CustomerDiscount();
		d.setLevel(level);
		d.setDiscount(percent);
		d.setFamilyLimit(family);
		d.setStartDate(startDate);
		updateCustomerDiscounts(d);
		return d;
	}
}
