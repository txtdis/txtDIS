package ph.txtdis.dyvek.service;

import ph.txtdis.dto.Remittance;
import ph.txtdis.info.Information;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

public interface VendorBillingService
	extends BillingService {

	String getCashAdvance();

	LocalDate getCashAdvanceDate();

	Long getCashAdvanceId();

	BigDecimal getCashAdvanceValue();

	BigDecimal getCheckValue();

	String getColor();

	BigDecimal getGrossWeight();

	BigDecimal getIodineValue();

	String getPaymentActedBy();

	ZonedDateTime getPaymentActedOn();

	BigDecimal getPercentFreeFattyAcid();

	BigDecimal getTareWeight();

	String getTruckPlateNo();

	String getTruckScaleNo();

	void setBillData(List<String> billData) throws Information, Exception;

	void setPaymentData(List<Remittance> paymentData) throws Information, Exception;
}
