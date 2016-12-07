package ph.txtdis.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ph.txtdis.dto.Named;

@Data
@Entity
@NoArgsConstructor
@Table(name = "pricing_type")
@EqualsAndHashCode(callSuper = true)
public class PricingTypeEntity extends AbstractNamedEntity<Long> implements Named {

	private static final long serialVersionUID = 8599562798765096281L;

	public PricingTypeEntity(String name) {
		this.name = name;
	}
}
