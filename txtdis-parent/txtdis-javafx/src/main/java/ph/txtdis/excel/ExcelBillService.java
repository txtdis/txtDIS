package ph.txtdis.excel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExcelBillService {

	LocalDate getBillDate();

	String getBillNo();

	String getBillTo();

	String getCheckedBy();

	String getCompanyAddress();

	String getCompanyName();

	short getLogoColor();

	String getLogoFont();

	String getNote();

	String getNotePrompt();

	String getPaymentType();

	List<String> getTableColumnHeaders();

	String getPreparedBy();

	String getReferenceNo();

	String getReferencePrompt();

	String getReviewedBy();

	BigDecimal getTotalValue();
}
