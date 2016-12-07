package ph.txtdis.service;

import org.springframework.stereotype.Service;

import ph.txtdis.domain.PricingTypeEntity;
import ph.txtdis.dto.PricingType;
import ph.txtdis.repository.PricingTypeRepository;

@Service("pricingTypeService")
public class PricingTypeServiceImpl
		extends AbstractCreateNameListService<PricingTypeRepository, PricingTypeEntity, PricingType>
		implements PricingTypeService {

	@Override
	public PricingTypeEntity findEntityByName(String text) {
		return repository.findByNameIgnoreCase(text);
	}

	@Override
	public PricingType toDTO(PricingTypeEntity e) {
		if (e == null)
			return null;
		PricingType p = new PricingType();
		p.setId(e.getId());
		p.setName(e.getName());
		p.setCreatedBy(e.getCreatedBy());
		p.setCreatedOn(e.getCreatedOn());
		return p;
	}

	@Override
	public PricingTypeEntity toEntity(PricingType t) {
		return t == null ? null : getEntity(t);
	}

	private PricingTypeEntity getEntity(PricingType t) {
		PricingTypeEntity p = repository.findByNameIgnoreCase(t.getName());
		return p != null ? p : newEntity(t.getName());
	}

	private PricingTypeEntity newEntity(String name) {
		PricingTypeEntity p = new PricingTypeEntity();
		p.setName(name);
		return p;
	}
}