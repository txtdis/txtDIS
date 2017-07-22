package ph.txtdis.mgdc.gsm.service;

import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.util.List;

import ph.txtdis.dto.ReceivingDetail;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.service.QtyPerUomService;
import ph.txtdis.type.UomType;

public interface ReceivingService extends QtyPerUomService {

	List<? extends ReceivingDetail> getDetails();

	Item getItem();

	BommedDiscountedPricedValidatedItemService getItemService();

	List<? extends ReceivingDetail> getPickedDetails();

	@Override
	default BigDecimal getQtyPerUom(UomType uom) {
		return getItemService().getQtyPerUom(getItem(), uom);
	}

	<T extends ReceivingDetail> T getReceivingDetail();

	default List<String> listReceivableItemNames() {
		List<String> l = getPickedDetails().stream().map(d -> d.getItemName()).collect(toList());
		return l == null ? null : l.stream().filter(n -> nonReturnedItem(n)).collect(toList());
	}

	default boolean nonReturnedItem(String n) {
		return getDetails() == null ? true
				: !getDetails().stream().anyMatch(d -> d.getReturnedQty() != null && n.equals(d.getItemName()));
	}

	void setItem(Item item);

	void setReceivingDetail(ReceivingDetail detail);

	default void setReceivingDetailAndItsItem(String itemName) throws Exception {
		if (itemName == null || itemName.isEmpty())
			return;
		setReceivingDetail(getPickedDetails().stream().filter(d -> d.getItemName().equals(itemName)).findAny().get());
		setItem(getItemService().findById(getReceivingDetail().getId()));
	}

	default <T extends ReceivingDetail> T updateReceivingDetailReturnedQty(BigDecimal qty) {
		getReceivingDetail().setReturnedQty(qty);
		return getReceivingDetail();
	}
}
