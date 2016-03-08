package ph.txtdis.domain;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
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
@EqualsAndHashCode(callSuper = true)
@Table(indexes = { @Index(columnList = "name") })
public class Item extends ModificationTracked<Long> {

	private static final long serialVersionUID = -3012020260825126952L;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private ItemType type;

	@ManyToOne
	private ItemFamily family;

	@Column(name = "vendor_id")
	private String vendorId;

	@Column(name = "is_not_discounted")
	private boolean notDiscounted;

	@Column(name = "end_of_life")
	private LocalDate endOfLife;

	@JoinColumn(name = "item_id")
	@OneToMany(fetch = EAGER, cascade = ALL)
	private List<QtyPerUom> qtyPerUomList;

	@JoinColumn(name = "item_id")
	@OneToMany(cascade = ALL)
	private List<Price> priceList;

	@JoinColumn(name = "item_id")
	@OneToMany(cascade = ALL)
	private List<VolumeDiscount> volumeDiscounts;

	@JoinColumn(name = "item_id")
	@OneToMany(cascade = ALL)
	private List<Bom> boms;

	@JoinColumn(name = "stock_id")
	@OneToOne(cascade = ALL)
	private Stock stock;
}
