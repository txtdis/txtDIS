package ph.txtdis.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "pricing_type")
@EqualsAndHashCode(callSuper = true)
public class PricingType extends Named<Long> {

	private static final long serialVersionUID = 8599562798765096281L;
}
