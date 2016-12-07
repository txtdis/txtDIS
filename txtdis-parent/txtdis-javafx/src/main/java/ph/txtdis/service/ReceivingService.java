package ph.txtdis.service;

import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.util.List;

import ph.txtdis.dto.Item;
import ph.txtdis.dto.ReceivingDetail;
import ph.txtdis.type.UomType;

public interface ReceivingService extends QtyPerUomService {

	List<? extends ReceivingDetail> getDetails();

	Item getItem();

	ItemService getItemService();

	List<? extends ReceivingDetail> getOriginalDetails();

	@Override
	default BigDecimal getQtyPerUom(UomType uom) {
		return getItemService().getQtyPerUom(getItem(), uom);
	}

	<T extends ReceivingDetail> T getReceivingDetail();

	default List<String> listReceivableItemNames() {
		List<String> l = getOriginalDetails().stream().map(d -> d.getItemName()).collect(toList());
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
		setReceivingDetail(getOriginalDetails().stream().filter(d -> d.getItemName().equals(itemName)).findAny().get());
		setItem(getItemService().find(getReceivingDetail().getId()));
	}

	default <T extends ReceivingDetail> T updateReceivingDetailReturnedQty(BigDecimal qty) {
		getReceivingDetail().setReturnedQty(qty);
		return getReceivingDetail();
	}
}
