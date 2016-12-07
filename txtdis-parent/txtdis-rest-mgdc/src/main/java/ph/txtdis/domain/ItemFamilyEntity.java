package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ph.txtdis.dto.Named;
import ph.txtdis.type.ItemTier;
import ph.txtdis.type.UomType;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "item_family", //
		indexes = { @Index(columnList = "name") })
public class ItemFamilyEntity extends AbstractNamedEntity<Long> implements Named {

	private static final long serialVersionUID = 1590751261303247800L;

	@Column(nullable = false)
	private ItemTier tier;

	private UomType uom;

	public ItemFamilyEntity(String name, ItemTier tier, UomType uom) {
		this.name = name;
		this.tier = tier;
		this.uom = uom;
	}

	@Override
	public String toString() {
		return name + "-" + tier + ", " + uom;
	}
}
