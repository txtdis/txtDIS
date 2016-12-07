package ph.txtdis.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.CreditNote;
import ph.txtdis.dto.CreditNoteDump;
import ph.txtdis.dto.CreditNotePayment;
import ph.txtdis.fx.table.AppTable;

public interface CreditNoteService extends Excel<CreditNote>, Reset, Serviced<Long> {

	CreditNotePayment createPayment(LocalDate d, String reference, BigDecimal payment, String remarks);

	BigDecimal getBalance();

	List<CreditNoteDump> getDataDump();

	String getPartiallyPaidHeaderText();

	BigDecimal getPayment();

	BigDecimal getTotal();

	void saveDataDumpAsExcel(AppTable<CreditNoteDump> t) throws IOException;

	void updatePayments(List<CreditNotePayment> l);

	void updateTotals(BigDecimal total);
}