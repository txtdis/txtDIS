package ph.txtdis.mgdc.ccbpi.service;

import ph.txtdis.dto.ReceivingDetail;
import ph.txtdis.mgdc.ccbpi.dto.Item;
import ph.txtdis.service.QtyPerUomService;
import ph.txtdis.type.UomType;

import java.math.BigDecimal;
import java.util.List;

import static java.util.stream.Collectors.toList;

public interface ReceivingService //
	extends QtyPerUomService {

	@Override
	default BigDecimal getQtyPerUom(UomType uom) {
		return getItemService().getQtyPerUom(getItem(), uom);
	}

	BommedDiscountedPricedValidatedItemService getItemService();

	Item getItem();

	void setItem(Item item);

	default List<String> listReceivableItemNames() {
		List<String> l = getPickedDetails().stream().map(d -> d.getItemName()).collect(toList());
		return l == null ? null : l.stream().filter(n -> nonReturnedItem(n)).collect(toList());
	}

	List<? extends ReceivingDetail> getPickedDetails();

	default boolean nonReturnedItem(String n) {
		return getDetails() == null ? true :
			!getDetails().stream().anyMatch(d -> d.getReturnedQty() != null && n.equals(d.getItemName()));
	}

	List<? extends ReceivingDetail> getDetails();

	default void setReceivingDetailAndItsItem(String itemName) throws Exception {
		if (itemName == null || itemName.isEmpty())
			return;
		setReceivingDetail(getPickedDetails().stream().filter(d -> d.getItemName().equals(itemName)).findAny().get());
		setItem(getItemService().findById(getReceivingDetail().getId()));
	}

	<T extends ReceivingDetail> T getReceivingDetail();

	void setReceivingDetail(ReceivingDetail detail);

	default <T extends ReceivingDetail> T updateReceivingDetailReturnedQty(BigDecimal qty) {
		getReceivingDetail().setReturnedQty(qty);
		return getReceivingDetail();
	}
}
