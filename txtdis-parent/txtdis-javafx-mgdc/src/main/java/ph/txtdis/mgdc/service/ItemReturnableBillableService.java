package ph.txtdis.mgdc.service;

import java.time.LocalDate;

public interface ItemReturnableBillableService {

	void clearItemReturnPaymentDataSetByItsInputDialogDuringDataEntry();

	void saveItemReturnReceiptData() throws Exception;

	void setItemReturnPaymentData(LocalDate paymentDate);
}
