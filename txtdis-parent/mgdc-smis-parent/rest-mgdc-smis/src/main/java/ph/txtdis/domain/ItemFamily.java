package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.ItemTier;
import ph.txtdis.type.UomType;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "item_family", indexes = { @Index(columnList = "name") })
public class ItemFamily extends Named<Long> {

	private static final long serialVersionUID = 1590751261303247800L;

	@Column(nullable = false)
	private ItemTier tier;

	private UomType uom;
}
