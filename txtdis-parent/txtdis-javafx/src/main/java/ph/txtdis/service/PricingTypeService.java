package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.PricingType;

public interface PricingTypeService
		extends Listed<PricingType>, SavedByName<PricingType>, Titled, UniquelyNamed<PricingType> {

	List<String> listNames();

	boolean isOffSite();
}