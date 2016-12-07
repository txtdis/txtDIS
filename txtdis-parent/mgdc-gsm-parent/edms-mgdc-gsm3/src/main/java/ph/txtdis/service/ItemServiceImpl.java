package ph.txtdis.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.EdmsInventory;
import ph.txtdis.domain.EdmsItem;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Item;
import ph.txtdis.repository.EdmsInventoryRepository;
import ph.txtdis.util.Code;

@Service("itemService")
public class ItemServiceImpl extends AbstractItemService {

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
		v.setUomCode(Code.BTL);
		v.setQty(BigDecimal.ONE);
		v.setPriceValue(getPricePerBottle(i));
		v.setUomLevelCode("2");
		v = edmsInventoryRepository.save(v);
		return v.getPriceValue();
	}

	private void newCaseInventory(Item i, BigDecimal pricePerBottle) {
		EdmsInventory e = newInventory(i);
		e.setUomCode(Code.CS);
		e.setQty(getBottlesPerCase(i));
		e.setPriceValue(e.getQty().multiply(pricePerBottle));
		e.setUomLevelCode("1");
		edmsInventoryRepository.save(e);
	}

	private EdmsInventory newInventory(Item i) {
		EdmsInventory e = new EdmsInventory();
		e.setItemCode(i.getVendorId());
		e.setCostValue(BigDecimal.ZERO);
		e.setGoodQty(BigDecimal.ZERO);
		e.setBadQty(BigDecimal.ZERO);
		return e;
	}

	@Override
	protected BigDecimal updatePricePerBottle(Item i) {
		EdmsInventory e = edmsInventoryRepository.findByItemCodeAndUomCode(i.getVendorId(), Code.BTL);
		e.setPriceValue(getPricePerBottle(i));
		e = edmsInventoryRepository.save(e);
		return e.getPriceValue();
	}

	@Override
	protected void updatePricePerCase(Item i) {
		String id = i.getVendorId();
		EdmsInventory cs = edmsInventoryRepository.findByItemCodeAndUomCode(id, Code.CS);
		EdmsInventory pc = edmsInventoryRepository.findByItemCodeAndUomCode(id, Code.BTL);
		cs.setPriceValue(pc.getQty().multiply(pc.getPriceValue()));
		edmsInventoryRepository.save(cs);
	}

	@Override
	protected BigDecimal getPricePerCase(String itemVendorNo) {
		EdmsInventory e = edmsInventoryRepository.findByItemCodeAndUomCode(itemVendorNo, Code.CS);
		return e.getPriceValue();
	}

	@Override
	public BigDecimal getPricePerBottle(BillableDetail d) {
		EdmsInventory e = edmsInventoryRepository.findByItemCodeAndUomCode(d.getItemVendorNo(), Code.BTL);
		return e.getPriceValue();
	}

	@Override
	protected BigDecimal getPricePerCase(EdmsItem e) {
		EdmsInventory i = edmsInventoryRepository.findByItemCodeAndUomCode(e.getCode(), Code.CS);
		return i.getPriceValue();
	}

	@Override
	protected BigDecimal getBottlesPerCase(String itemCode) {
		EdmsInventory e = edmsInventoryRepository.findByItemCodeAndUomCode(itemCode, Code.BTL);
		return e.getQty();
	}
}