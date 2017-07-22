package ph.txtdis.dyvek.service;

import java.math.BigDecimal;
import java.util.List;

import ph.txtdis.dyvek.model.Billable;

public interface DeliveryService //
		extends OrderService {

	List<Billable> findUnpaidBillings(String client);

	String getColor();

	BigDecimal getIodineValue();

	BigDecimal getMoistureContent();

	BigDecimal getPercentLauricFreeFattyAcid();

	BigDecimal getPercentOleicFreeFattyAcid();

	BigDecimal getSaponificationIndex();

	String getTruckPlateNo();

	String getTruckScaleNo();

	List<Billable> listDeliveries(Billable b);

	List<String> listRecipients();

	void setColor(String color);

	void setIodineValue(BigDecimal iv);

	void setMoistureContent(BigDecimal mvm);

	void setPercentLauricFFA(BigDecimal ffa);

	void setPercentOleicFFA(BigDecimal ffa);

	void setRecipient(String name);

	void setSaponificationIndex(BigDecimal si);

	void setTruckPlateNo(String plate);

	void setTruckScaleNo(String scale);
}
