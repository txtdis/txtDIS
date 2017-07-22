package ph.txtdis.mgdc.gsm.printer;

import static org.apache.commons.lang3.StringUtils.leftPad;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.Fraction;

import ph.txtdis.mgdc.gsm.domain.ItemEntity;
import ph.txtdis.mgdc.gsm.service.server.ItemService;

public interface QtyInFraction {

	default String qtyInFraction(ItemService itemService, ItemEntity e, int quantity) {
		String qty = Fraction.getFraction(quantity, itemService.getCountPerCase(e)).toProperString();
		String fraction = StringUtils.substringAfter(qty, " ");
		if (fraction.isEmpty() && !qty.contains("/"))
			qty = qty + "      ";
		else if (fraction.contains("/") && fraction.length() == 4)
			qty = qty.replace(" ", "  ");
		return leftPad(qty + " ", 11);
	}
}
