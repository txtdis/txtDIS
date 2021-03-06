package ph.txtdis.mgdc.ccbpi.domain;

import static java.util.Collections.emptyList;
import static javax.persistence.CascadeType.ALL;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.mgdc.domain.AbstractItemEntity;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "item")
public class ItemEntity //
	extends AbstractItemEntity {

	private static final long serialVersionUID = -3012020260825126952L;

	@ManyToOne
	private ItemEntity empties;

	@Column(name = "vendor_id")
	private String vendorId;

	@OneToMany(mappedBy = "item", cascade = ALL)
	private List<QtyPerUomEntity> qtyPerUomList;

	@OneToMany(mappedBy = "item", cascade = ALL)
	private List<BomEntity> boms;

	public List<BomEntity> getBoms() {
		return boms == null ? emptyList() : boms;
	}

	@Override
	public String toString() {
		return getName();
	}
}
