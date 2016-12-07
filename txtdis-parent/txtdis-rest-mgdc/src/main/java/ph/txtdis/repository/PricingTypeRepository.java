package ph.txtdis.repository;

import org.springframework.stereotype.Repository;

import ph.txtdis.domain.PricingTypeEntity;
import ph.txtdis.repository.NameListRepository;

@Repository("pricingTypeRepository")
public interface PricingTypeRepository extends NameListRepository<PricingTypeEntity> {
}
