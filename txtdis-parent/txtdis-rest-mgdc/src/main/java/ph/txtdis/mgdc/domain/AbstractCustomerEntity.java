package ph.txtdis.mgdc.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractDeactivatableModifiableNamedCreatedKeyedEntity;
import ph.txtdis.type.PartnerType;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractCustomerEntity //
	extends AbstractDeactivatableModifiableNamedCreatedKeyedEntity<Long> {

	private static final long serialVersionUID = 6232176995609508579L;

	private PartnerType type;

	@ManyToOne
	@JoinColumn(name = "primary_pricing")
	private PricingTypeEntity primaryPricingType;

	public String getIdNo() {
		return getId() == null ? null : getId().toString();
	}
}
