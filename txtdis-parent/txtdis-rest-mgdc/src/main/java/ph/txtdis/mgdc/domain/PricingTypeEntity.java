package ph.txtdis.mgdc.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractNamedCreatedKeyedEntity;
import ph.txtdis.dto.Named;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "pricing_type")
@EqualsAndHashCode(callSuper = false)
public class PricingTypeEntity //
	extends AbstractNamedCreatedKeyedEntity<Long> //
	implements Named {

	private static final long serialVersionUID = 8599562798765096281L;

	@Override
	public String toString() {
		return getName();
	}
}
