package ph.txtdis.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.CreationTracked;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.PaymentHistory;
import ph.txtdis.dto.Remittance;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.info.Information;
import ph.txtdis.type.BillingType;
import ph.txtdis.type.PaymentType;

public interface RemittanceService
		extends Detailed, DecisionNeeded, Reset, Serviced<Long>, Spreadsheet<PaymentHistory>, CreationTracked {

	RemittanceDetail createDetail(Billable billable, BigDecimal payment, BigDecimal remaining);

	boolean isBillableOnThisPaymentList(Billable i);

	List<String> getBankNames();

	List<Customer> getBanks();

	Long getCheckId();

	List<String> getCollectorNames();

	List<String> getCollectors();

	ZonedDateTime getDepositedOn();

	String getDepositor();

	String getDepositorBank();

	ZonedDateTime getDepositorOn();

	List<RemittanceDetail> getDetails();

	List<Customer> getDraweeBanks();

	LocalDate getPaymentDate();

	PaymentType[] getPaymentTypes();

	String getReceivedBy();

	ZonedDateTime getReceivedOn();

	BigDecimal getRemaining();

	Remittance getUndepositedPayment(PaymentType t, String seller, LocalDate d) throws Exception;

	BigDecimal getValue();

	boolean isCashPayment();

	boolean isOffSite();

	boolean isUserAllowedToMakeCashDeposits();

	boolean isUserAllowedToMakeCheckDeposits();

	boolean isUserAllowedToPostRemittanceData();

	boolean isUserAllowedToReceiveFundTransfer();

	void nullifyPaymentData(Billable billable) throws Information, Exception;

	void open(Customer bank, Long checkId) throws Exception;

	void resetInputDataRelatedToPayment();

	@Override
	default void save() throws Information, Exception {
		Serviced.super.save();
	}

	void save(List<Remittance> l) throws Information, Exception;

	void setCheckId(Long id);

	void setCollector(String s);

	void setDepositData(Customer bank, ZonedDateTime depositedOn);

	void setDraweeBank(String c);

	void setFundTransferData();

	void setPayment(BigDecimal p);

	void setRemarks(String text);

	Billable updateUponIdValidation(BillingType b, String prefix, Long id, String suffix) throws Exception;

	void validateBankCheckBeforeSetting(Customer bank) throws Exception;

	void validateCashCollection() throws Exception;

	void validateOrderDateBeforeSetting(LocalDate d) throws Exception;
}