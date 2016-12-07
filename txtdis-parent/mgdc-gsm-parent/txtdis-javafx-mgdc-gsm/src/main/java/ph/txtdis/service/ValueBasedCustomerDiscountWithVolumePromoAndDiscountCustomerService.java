package ph.txtdis.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.CustomerDiscount;
import ph.txtdis.dto.CustomerVolumeDiscount;
import ph.txtdis.dto.CustomerVolumePromo;
import ph.txtdis.dto.Item;
import ph.txtdis.exception.DateInThePastException;
import ph.txtdis.exception.DuplicateException;

public interface ValueBasedCustomerDiscountWithVolumePromoAndDiscountCustomerService extends QtyPerUomService {

	CustomerDiscount createDiscountUponValidation(Item item, BigDecimal discount, LocalDate start)
			throws DateInThePastException, DuplicateException;

	CustomerVolumeDiscount createVolumeDiscountUponValidation(BigDecimal targetQty, BigDecimal discount, LocalDate start)
			throws DateInThePastException, DuplicateException;

	CustomerVolumePromo createVolumePromoUponValidation(BigDecimal targetQty, BigDecimal freeQty, LocalDate start)
			throws DateInThePastException, DuplicateException;

	List<CustomerVolumeDiscount> getVolumeDiscounts();

	List<CustomerVolumePromo> getVolumePromos();

	void setItem(Item item);

	void setVolumePromos(List<CustomerVolumePromo> promos);

	void setVolumeDiscounts(List<CustomerVolumeDiscount> discounts);
}
