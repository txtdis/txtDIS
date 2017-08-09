package ph.txtdis.mgdc.gsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Bom;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.service.RestClientService;

import java.math.BigDecimal;

@Service("itemService")
public class ItemServiceImpl //
	extends AbstractBommedDiscountedPricedValidatedItemService //
	implements ExpandedBommedDiscountedPricedValidatedItemService {

	@Autowired
	private RestClientService<Bom> bomRestClientService;

	@Override
	public BigDecimal getBomExpandedQtyInCases(BillableDetail d) {
		BigDecimal qty = d.getInitialQtyInCases();
		try {
			Bom bom = bomRestClientService.module("bom")
				.getOne("/expandedQtyInCases?id=" + d.getId() + "&name=" + d.getItemName() + "&qty=" + qty);
			return bom.getQty();
		} catch (Exception e) {
			return qty;
		}
	}

	@Override
	public void openByOpenDialogInputtedKey(String key) throws Exception {
		try {
			openByDoubleClickedTableCellId(Long.valueOf(key));
		} catch (NumberFormatException e) {
			throw new NotFoundException("Item No. " + key);
		}
	}
}
