package ph.txtdis.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import ph.txtdis.dto.CreationLogged;
import ph.txtdis.dto.Remittance;
import ph.txtdis.info.Information;
import ph.txtdis.type.PaymentType;

public interface RemittanceService //
		extends BankDrawnCheckService<Remittance>, DecisionNeededService, OpenDialogHeaderTextService, ResettableService,
		RemarkedAndSpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService<Long>, Spreadsheet<Remittance>,
		CreationLogged {

	boolean canDepositCash();

	boolean canDepositCheck();

	boolean canPostPaymentData();

	boolean canReceiveTransferredPayments();

	Long getCheckId();

	List<String> getCollectors();

	ZonedDateTime getDepositedOn();

	String getDepositedTo();

	String getDepositEncodedBy();

	ZonedDateTime getDepositEncodedOn();

	List<String> getDraweeBanks();

	LocalDate getPaymentDate();

	PaymentType[] getPaymentTypes();

	String getReceivedBy();

	List<String> getReceivedFromList();

	ZonedDateTime getReceivedOn();

	Remittance getUndepositedPayment(PaymentType t, String seller, LocalDate d) throws Exception;

	BigDecimal getValue();

	boolean isCashPayment();

	List<Remittance> list(String bank);

	void open(String bank, Long checkId) throws Exception;

	void resetInputDataRelatedToPayment();

	void save(List<Remittance> l) throws Information, Exception;

	void setCheckId(Long id);

	void setCollector(String s);

	void setDepositData(String bank, ZonedDateTime depositedOn);

	void setDraweeBank(String c);

	void setFundTransferData();

	void setPayment(BigDecimal p);

	void setPaymentDate(LocalDate d);

	void validateBankCheckBeforeSetting(String bank) throws Exception;

	void validateCashCollection() throws Exception;

	void validateOrderDateBeforeSetting(LocalDate d) throws Exception;
}