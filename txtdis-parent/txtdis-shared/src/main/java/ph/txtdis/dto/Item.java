package ph.txtdis.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.ItemType;

@Data
@EqualsAndHashCode(callSuper = true)
public class Item extends AbstractModificationTracked<Long> {

	private boolean notDiscounted;

	private String description, empties, name, vendorId;

	private ItemFamily family;

	private ItemType type;

	private List<Bom> boms;

	private List<Price> priceList;

	private List<QtyPerUom> qtyPerUomList;

	private List<VolumeDiscount> volumeDiscounts;

	private LocalDate endOfLife;

	private VolumeDiscount latestVolumeDiscount;

	private BigDecimal latestPrimarySellingUnitPrice;

	@Override
	public String toString() {
		return name;
	}
}
