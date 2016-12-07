package ph.txtdis.domain;

import static javax.persistence.CascadeType.ALL;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.ItemType;

@Data
@Entity
@Table(name = "item")
@EqualsAndHashCode(callSuper = true)
public class ItemEntity extends AbstractModifiedEntity<Long> implements Comparator<ItemEntity> {

	private static final long serialVersionUID = -3012020260825126952L;

	@Column(nullable = false)
	private String description;

	@ManyToOne
	private ItemEntity empties;

	@Column(nullable = false)
	private ItemType type;

	@ManyToOne
	private ItemFamilyEntity family;

	@Column(name = "vendor_id")
	private String vendorId;

	@Column(name = "is_not_discounted")
	private boolean notDiscounted;

	@Column(name = "end_of_life")
	private LocalDate endOfLife;

	@OneToMany(mappedBy = "item", cascade = ALL)
	private List<QtyPerUomEntity> qtyPerUomList;

	@JoinColumn(name = "item_id")
	@OneToMany(cascade = ALL)
	private List<PriceEntity> priceList;

	@JoinColumn(name = "item_id")
	@OneToMany(cascade = ALL)
	private List<VolumeDiscountEntity> volumeDiscounts;

	@OneToMany(mappedBy = "item", cascade = ALL)
	private List<BomEntity> boms;

	@JoinColumn(name = "stock_id")
	@OneToOne(cascade = ALL)
	private StockEntity stock;

	@Override
	public String toString() {
		return vendorId + "-" + description + ", " + type + ", " + family;
	}

	@Override
	public int compare(ItemEntity a, ItemEntity b) {
		return a.getId().compareTo(b.getId());
	}
}
