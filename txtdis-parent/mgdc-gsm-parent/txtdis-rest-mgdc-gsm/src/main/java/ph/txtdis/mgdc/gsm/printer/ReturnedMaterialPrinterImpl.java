package ph.txtdis.mgdc.gsm.printer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ph.txtdis.mgdc.gsm.domain.BillableDetailEntity;
import ph.txtdis.mgdc.gsm.service.server.QtyPerUomService;

import java.math.BigDecimal;

import static ph.txtdis.type.UomType.CS;
import static ph.txtdis.util.NumberUtils.divide;

@Component("returnedMaterialPrinter")
public class ReturnedMaterialPrinterImpl //
	extends AbstractReturnedMaterialPrinter {

	@Autowired
	private QtyPerUomService uomService;

	@Override
	protected BigDecimal initialQty(BillableDetailEntity d) {
		return divide(d.getInitialQty(), uomService.getItemQtyPerUom(d.getItem(), CS));
	}
}
