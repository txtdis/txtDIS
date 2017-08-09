package ph.txtdis.dyvek.service;

import ph.txtdis.dyvek.model.Billable;

import java.math.BigDecimal;
import java.util.List;

public interface DeliveryService //
	extends OrderService {

	List<Billable> findUnpaidBillings(String client);

	String getColor();

	void setColor(String color);

	BigDecimal getGrossWeight();

	void setGrossWeight(BigDecimal gross);

	BigDecimal getIodineValue();

	void setIodineValue(BigDecimal iv);

	BigDecimal getPercentFreeFattyAcid();

	void setPercentFreeFattyAcid(BigDecimal ffa);

	BigDecimal getTareWeight();

	String getTruckPlateNo();

	void setTruckPlateNo(String plate);

	String getTruckScaleNo();

	void setTruckScaleNo(String scale);

	List<Billable> listDeliveries(Billable b);

	List<String> listRecipients();

	void setRecipient(String name);
}
