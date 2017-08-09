package ph.txtdis.mgdc.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractDeactivatableModifiableNamedCreatedKeyedEntity;
import ph.txtdis.type.ItemType;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractItemEntity //
	extends AbstractDeactivatableModifiableNamedCreatedKeyedEntity<Long> {

	private static final long serialVersionUID = -1161314009708169352L;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private ItemType type;

	@ManyToOne
	private ItemFamilyEntity family;

	@Column(name = "vendor_id")
	private String vendorId;

	@Column(name = "is_not_discounted")
	private Boolean notDiscounted;

	@Column(name = "end_of_life")
	private LocalDate endOfLife;

	@JoinColumn(name = "item_id")
	@OneToMany(cascade = ALL)
	private List<PriceEntity> priceList;
}
