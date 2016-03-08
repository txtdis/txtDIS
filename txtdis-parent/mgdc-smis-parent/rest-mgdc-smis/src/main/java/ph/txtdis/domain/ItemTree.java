package ph.txtdis.domain;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "item_tree", //
		indexes = { //
				@Index(columnList = "family_id, parent_id"), //
				@Index(columnList = "family_id") }, //
		uniqueConstraints = @UniqueConstraint(columnNames = { "family_id", "parent_id" }) )
public class ItemTree extends CreationTracked<Long> {

	private static final long serialVersionUID = 4058968729625611538L;

	@ManyToOne(optional = false)
	private ItemFamily family;

	@ManyToOne(optional = false)
	private ItemFamily parent;

	@Override
	public String toString() {
		return family + " member of " + parent;
	}
}
