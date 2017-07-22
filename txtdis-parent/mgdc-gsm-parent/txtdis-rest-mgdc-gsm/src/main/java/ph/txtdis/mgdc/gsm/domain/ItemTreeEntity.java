package ph.txtdis.mgdc.gsm.domain;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ph.txtdis.domain.AbstractCreatedKeyedEntity;
import ph.txtdis.mgdc.domain.ItemFamilyEntity;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "item_tree", //
		indexes = { //
				@Index(columnList = "family_id, parent_id"), //
				@Index(columnList = "family_id") }, //
		uniqueConstraints = @UniqueConstraint(columnNames = { "family_id", "parent_id" }))
public class ItemTreeEntity extends AbstractCreatedKeyedEntity<Long> {

	private static final long serialVersionUID = 4058968729625611538L;

	@ManyToOne(optional = false)
	private ItemFamilyEntity family;

	@ManyToOne(optional = false)
	private ItemFamilyEntity parent;

	@Override
	public String toString() {
		return family + " member of " + parent;
	}
}
