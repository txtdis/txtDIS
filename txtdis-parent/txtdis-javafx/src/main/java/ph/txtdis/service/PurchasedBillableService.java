package ph.txtdis.service;

import java.math.BigDecimal;

import ph.txtdis.type.UomType;

public interface PurchasedBillableService {

	Integer getOnPurchaseDaysLevel() throws Exception;

	Integer getOnReceiptDaysLevel(UomType uom, BigDecimal qty);
}
