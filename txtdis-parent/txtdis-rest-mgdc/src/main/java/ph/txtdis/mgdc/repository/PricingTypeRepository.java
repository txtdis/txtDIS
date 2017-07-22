package ph.txtdis.mgdc.repository;

import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.domain.PricingTypeEntity;
import ph.txtdis.repository.NameListRepository;

@Repository("pricingTypeRepository")
public interface PricingTypeRepository extends NameListRepository<PricingTypeEntity> {
}
