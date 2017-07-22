package ph.txtdis.dyvek.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import ph.txtdis.dyvek.model.BillableDetail;

public interface BillingService //
		extends OrderService {

	boolean areAssignedAndDeliverQtyDifferent();

	BigDecimal getAdjustmentPrice();

	BigDecimal getAdjustmentQty();

	BigDecimal getAdjustmentValue();

	String getBank();

	String getBillActedByBy();

	ZonedDateTime getBillActedOn();

	LocalDate getBillDate();

	String getBillNo();

	LocalDate getCheckDate();

	Long getCheckId();

	BigDecimal getNetValue();

	void setDetails(List<BillableDetail> l);

	void updateTotals(List<BillableDetail> items);
}
