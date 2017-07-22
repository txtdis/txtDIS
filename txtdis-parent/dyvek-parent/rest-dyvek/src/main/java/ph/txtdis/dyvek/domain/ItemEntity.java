package ph.txtdis.dyvek.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ph.txtdis.domain.AbstractCreatedKeyedEntity;
import ph.txtdis.dto.Named;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "item", //
		uniqueConstraints = @UniqueConstraint(name = "item_name_key", columnNames = { "name" }))
public class ItemEntity //
		extends AbstractCreatedKeyedEntity<Long> //
		implements Named {

	private static final long serialVersionUID = -4451029922095069033L;

	@Column(nullable = false)
	private String name;

	private String description;

	@Override
	public String toString() {
		return getName();
	}
}
