package ph.txtdis.service;

import ph.txtdis.dto.PricingType;

public interface DealerPriced {

	default PricingType dealerPrice() {
		PricingType t = new PricingType();
		t.setName("DEALER");
		return t;
	}
}
