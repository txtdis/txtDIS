package ph.txtdis.repository;

import org.springframework.stereotype.Repository;

import ph.txtdis.domain.PricingType;

@Repository("pricingTypeRepository")
public interface PricingTypeRepository extends NameListRepository<PricingType> {
}
