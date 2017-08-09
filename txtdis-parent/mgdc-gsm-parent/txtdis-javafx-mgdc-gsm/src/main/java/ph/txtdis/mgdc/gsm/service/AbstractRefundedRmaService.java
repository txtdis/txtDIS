package ph.txtdis.mgdc.gsm.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Remittance;
import ph.txtdis.exception.DuplicateCheckException;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.service.AdjustableInputtedPaymentDetailedRemittanceService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.math.BigDecimal.ZERO;
import static org.apache.log4j.Logger.getLogger;

public abstract class AbstractRefundedRmaService //
	extends AbstractRmaService //
	implements RefundedRmaService {

	private static Logger logger = getLogger(AbstractRefundedRmaService.class);

	@Autowired
	private AdjustableInputtedPaymentDetailedRemittanceService remitService;

	@Autowired
	private VatService vatService;

	@Override
	public void clearInputtedReturnPaymentData() {
		setThreePartId(null, null, null);
		remitService.reset();
	}

	@Override
	public String[] getCheckDetails() {
		Remittance r = remitService.findByBillable(get());
		return r == null ? null : new String[]{r.getDraweeBank(), r.getCheckId().toString()};
	}

	@Override
	public String getInvoiceNo() {
		return get().getOrderNo();
	}

	@Override
	public BigDecimal getVat() {
		return vatService.getVat(getTotalValue());
	}

	@Override
	public BigDecimal getVatable() {
		return vatService.getVatable(getTotalValue());
	}

	@Override
	public void print() throws Exception {
		Billable b = getRestClientService().module(getModuleName()).getOne("/print?id=" + getId());
		set(b);
	}

	@Override
	public void saveReturnPaymentData(LocalDate d) throws Information, Exception {
		saveInvoiceDetails();
		saveReturnPaymentDetails(d);
	}

	private void saveInvoiceDetails() throws Exception {
		logger.info("\n    Invoice@saveInvoiceDetails = " + get());
		logger.info("\n    Value@saveInvoiceDetails = " + get().getTotalValue());
		get().setBilledBy(getUsername());
		set(save(get()));
	}

	private void saveReturnPaymentDetails(LocalDate d) throws Exception, Information {
		remitService.validateOrderDateBeforeSetting(d);
		remitService.setPayment(positiveTotal());
		remitService.setCollector(getAbbreviatedModuleNoPrompt() + getModuleNo());
		remitService.createDetail(positiveTotalRma(), positiveTotal(), ZERO);
		logRemittanceDataToSave();
		remitService.save();
	}

	private BigDecimal positiveTotal() {
		return getTotalValue().negate();
	}

	private Billable positiveTotalRma() {
		Billable b = get();
		b.setTotalValue(positiveTotal());
		return b;
	}

	private void logRemittanceDataToSave() {
		Remittance r = remitService.get();
		logger.info("\n    ModuleNo@logRemittanceDataToSave = " + getSavingInfo());
		logger.info("\n    Date@logRemittanceDataToSave = " + r.getPaymentDate());
		logger.info("\n    Bank@logRemittanceDataToSave = " + r.getDraweeBank());
		logger.info("\n    Check@logRemittanceDataToSave = " + r.getCheckId());
		logger.info("\n    Payment@logRemittanceDataToSave = " + r.getValue());
		logger.info("\n    Invoice@logRemittanceDataToSave = " + r.getDetails());
	}

	@Override
	public void updateUponCheckIdValidation(String bank, Long checkId) throws Exception {
		verifyCheckHasNotBeenUsed(bank, checkId);
		setCheckDetails(bank, checkId);
	}

	private void verifyCheckHasNotBeenUsed(String bank, Long checkId) throws Exception, DuplicateCheckException {
		Remittance r = remitService.findByCheck(bank, checkId);
		if (r != null)
			throw new DuplicateCheckException(bank, checkId, r.getId());
	}

	private void setCheckDetails(String bank, Long checkId) {
		remitService.setDraweeBank(bank);
		remitService.setCheckId(checkId);
	}

	@Override
	public void updateUponInvoiceNoValidation(String prefix, Long id, String suffix) throws Exception {
		checkNoDuplicates(getRestClientService(), prefix, id, suffix);
		setThreePartId(prefix, id, suffix);
	}
}
