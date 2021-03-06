package ph.txtdis.mgdc.service;

import ph.txtdis.dto.QtyPerUom;
import ph.txtdis.type.UomType;

import java.math.BigDecimal;
import java.util.List;

public interface ValidatedUomService {

	QtyPerUom createQtyPerUom( //
	                           UomType uom, //
	                           BigDecimal qty, //
	                           Boolean isPurchased, //
	                           Boolean isSold, //
	                           Boolean isReported);

	boolean isPurchased();

	boolean isSold();

	List<UomType> listUoms();

	void validatePurchasedUom(Boolean isPurchased) throws Exception;

	void validateReportedUom(Boolean isReported) throws Exception;
}
