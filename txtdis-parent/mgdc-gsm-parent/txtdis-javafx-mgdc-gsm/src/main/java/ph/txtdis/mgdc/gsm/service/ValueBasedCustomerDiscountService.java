package ph.txtdis.mgdc.gsm.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import ph.txtdis.mgdc.gsm.dto.CustomerDiscount;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.service.QtyPerUomService;

public interface ValueBasedCustomerDiscountService
		extends QtyPerUomService {

	CustomerDiscount createDiscountUponValidation(Item item, BigDecimal discount, LocalDate start) throws Exception;

	List<CustomerDiscount> getCustomerDiscounts();

	void setCustomerDiscounts(List<CustomerDiscount> discounts);

	void setItem(Item item);
}
