package ph.txtdis.domain;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ph.txtdis.dto.Named;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "warehouse", //
		indexes = { @Index(columnList = "name") })
public class WarehouseEntity extends AbstractNamedEntity<Long> implements Named {

	private static final long serialVersionUID = -902162181604587549L;

	@ManyToOne
	private ItemFamilyEntity family;

	public WarehouseEntity(String name, ItemFamilyEntity family) {
		this.name = name;
		this.family = family;
	}
}
