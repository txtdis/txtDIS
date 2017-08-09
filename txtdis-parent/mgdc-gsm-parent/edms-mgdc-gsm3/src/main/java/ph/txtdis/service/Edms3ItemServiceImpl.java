package ph.txtdis.service;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static ph.txtdis.util.Code.BTL;
import static ph.txtdis.util.Code.CS;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.EdmsInventory;
import ph.txtdis.domain.EdmsItem;
import ph.txtdis.domain.EdmsSalesOrderDetail;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.repository.EdmsInventoryRepository;
import ph.txtdis.util.Code;

@Service("itemService")
public class Edms3ItemServiceImpl //
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
		EdmsInventory e = edmsInventoryRepository.findByItemCodeAndUomCode(i.getVendorNo(), Code.BTL);
		if (e == null)
			return ZERO;
		e.setPriceValue(getPricePerBottle(i));
		e = edmsInventoryRepository.save(e);
		return e.getPriceValue();
	}

	@Override
	protected void updatePricePerCase(Item i) {
		String id = i.getVendorNo();
		EdmsInventory cs = edmsInventoryRepository.findByItemCodeAndUomCode(id, CS);
		if (cs == null)
			return;
		EdmsInventory pc = edmsInventoryRepository.findByItemCodeAndUomCode(id, BTL);
		cs.setPriceValue(pc.getQty().multiply(pc.getPriceValue()));
		edmsInventoryRepository.save(cs);
	}

	@Override
	public BigDecimal getPricePerBottle(EdmsSalesOrderDetail d) {
		EdmsInventory e = edmsInventoryRepository.findByItemCodeAndUomCode(d.getItemCode(), BTL);
		return e == null ? ZERO : e.getPriceValue();
	}

	@Override
	protected BigDecimal getPricePerCase(String itemVendorNo) {
		EdmsInventory e = edmsInventoryRepository.findByItemCodeAndUomCode(itemVendorNo, CS);
		return e == null ? ZERO : e.getPriceValue();
	}

	@Override
	protected BigDecimal getPricePerCase(EdmsItem e) {
		EdmsInventory i = edmsInventoryRepository.findByItemCodeAndUomCode(e.getCode(), CS);
		return i == null ? ZERO : i.getPriceValue();
	}

	@Override
	protected BigDecimal getBottlesPerCase(String itemCode) {
		EdmsInventory e = edmsInventoryRepository.findByItemCodeAndUomCode(itemCode, BTL);
		return e == null ? ZERO : e.getQty();
	}
}