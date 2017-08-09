package ph.txtdis.dyvek.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ph.txtdis.domain.AbstractCreatedKeyedEntity;
import ph.txtdis.dto.Named;
import ph.txtdis.type.PartnerType;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "customer", //
	indexes = { //
		@Index(name = "customer_name_idx", columnList = "name"), //
		@Index(name = "customer_type_idx", columnList = "type")}, //
	uniqueConstraints = @UniqueConstraint(name = "customer_name_type_key", columnNames = {"name", "type"}))
public class CustomerEntity //
	extends AbstractCreatedKeyedEntity<Long> //
	implements Named {

	private static final long serialVersionUID = -3469383165882339926L;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private PartnerType type;

	@Override
	public String toString() {
		return getName();
	}
}
