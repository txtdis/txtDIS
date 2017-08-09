package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.domain.EdmsInventory;
import ph.txtdis.domain.EdmsItem;
import ph.txtdis.domain.EdmsSalesOrderDetail;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.repository.EdmsInventoryRepository;
import ph.txtdis.util.Code;

import java.math.BigDecimal;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static ph.txtdis.util.Code.BTL;
import static ph.txtdis.util.Code.CS;

@Service("itemService")
public class Edms2ItemServiceImpl
	extends AbstractEdmsItemService {

	@Autowired
	private EdmsInventoryRepository edmsInventoryRepository;

	@Override
	protected EdmsItem createItem(Item i) {
		BigDecimal pricePerBottle = newBottleInventory(i);
		newCaseInventory(i, pricePerBottle);
		return toEdmsItem(i);
	}

	private BigDecimal newBottleInventory(Item i) {
		EdmsInventory v = newInventory(i);
		v.setUomCode(BTL);
		v.setQty(ONE);
		v.setPriceValue(getPricePerBottle(i));
		v.setUomLevelCode("2");
		v = edmsInventoryRepository.save(v);
		return v.getPriceValue();
	}

	private void newCaseInventory(Item i, BigDecimal pricePerBottle) {
		EdmsInventory e = newInventory(i);
		e.setUomCode(CS);
		e.setQty(getBottlesPerCase(i));
		e.setPriceValue(e.getQty().multiply(pricePerBottle));
		e.setUomLevelCode("1");
		edmsInventoryRepository.save(e);
	}

	private EdmsInventory newInventory(Item i) {
		EdmsInventory e = new EdmsInventory();
		e.setItemCode(i.getVendorNo());
		e.setCostValue(ZERO);
		e.setGoodQty(ZERO);
		e.setBadQty(ZERO);
		return e;
	}

	@Override
	protected BigDecimal updatePricePerBottle(Item i) {
		EdmsInventory e = item(i.getVendorNo(), BTL);
		if (e == null)
			return ZERO;
		e.setPriceValue(getPricePerBottle(i));
		e = edmsInventoryRepository.save(e);
		return e.getPriceValue();
	}

	private EdmsInventory item(String code, String uom) {
		return edmsInventoryRepository.findByItemCodeAndUomCode(code, uom);
	}

	@Override
	protected void updatePricePerCase(Item i) {
		EdmsInventory cs = item(i.getVendorNo(), CS);
		if (cs == null)
			return;
		EdmsInventory pc = item(i.getVendorNo(), BTL);
		cs.setPriceValue(pc.getQty().multiply(pc.getPriceValue()));
		edmsInventoryRepository.save(cs);
	}

	@Override
	protected BigDecimal getPricePerCase(String code) {
		EdmsInventory e = item(code, Code.CS);
		return e == null ? ZERO : e.getPriceValue();
	}

	@Override
	public BigDecimal getPricePerBottle(EdmsSalesOrderDetail d) {
		EdmsInventory e = item(d.getItemCode(), BTL);
		return e == null ? ZERO : e.getPriceValue();
	}

	@Override
	protected BigDecimal getPricePerCase(EdmsItem i) {
		EdmsInventory e = item(i.getCode(), CS);
		return e == null ? ZERO : e.getPriceValue();
	}

	@Override
	protected BigDecimal getBottlesPerCase(String itemCode) {
		EdmsInventory e = item(itemCode, Code.BTL);
		return e == null ? ZERO : e.getQty();
	}
}