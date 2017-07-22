package ph.txtdis.mgdc.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import ph.txtdis.dto.CreditNote;
import ph.txtdis.dto.CreditNoteDump;
import ph.txtdis.dto.CreditNotePayment;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.mgdc.fx.table.CreditNoteListTable;
import ph.txtdis.service.AppendableDetailService;
import ph.txtdis.service.DecisionNeededService;
import ph.txtdis.service.RemarkedAndSpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService;
import ph.txtdis.service.SavableAsExcelService;

public interface CreditNoteService //
		extends AppendableDetailService, DecisionNeededService, SavableAsExcelService<CreditNote>,
		RemarkedAndSpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService<Long> {

	void createAndWriteAnExcelDataDumpFile(AppTable<CreditNoteDump> t) throws IOException;

	CreditNotePayment createPayment(LocalDate d, String reference, BigDecimal payment, String remarks);

	BigDecimal getBalance();

	LocalDate getCreditDate();

	List<CreditNoteDump> getDataDump();

	String getDescription();

	String getLastModifiedBy();

	ZonedDateTime getLastModifiedOn();

	BigDecimal getPayment();

	List<CreditNotePayment> getPayments();

	String getReference();

	@Override
	String getRemarks();

	BigDecimal getTotal();

	String getUnpaidHeaderText();

	String getUnvalidatedHeaderText();

	List<CreditNote> listUnpaid();

	List<CreditNote> listUnvalidated();

	void setCreditDateUponUserValidation(LocalDate value) throws Exception;

	void setDescription(String text);

	void setReference(String text);

	void updatePayments(List<CreditNotePayment> l);

	void updateTotals(BigDecimal total);

	CreditNote validate(Long id) throws Exception;

	void writeUnpaidExcelFile(CreditNoteListTable table) throws IOException;

	void writeUnvalidatedExcelFile(CreditNoteListTable table) throws IOException;
}