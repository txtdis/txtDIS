package ph.txtdis.mgdc.ccbpi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.AbstractModificationTracked;
import ph.txtdis.dto.Bom;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.Price;
import ph.txtdis.dto.QtyPerUom;
import ph.txtdis.type.ItemType;

@Data
@EqualsAndHashCode(callSuper = true)
public class Item //
		extends AbstractModificationTracked<Long> {

	private boolean notDiscounted;

	private String description, empties, name, vendorNo;

	private ItemFamily family;

	private ItemType type;

	private List<Bom> boms;

	private List<Price> priceList;

	private List<QtyPerUom> qtyPerUomList;

	private LocalDate endOfLife;

	private BigDecimal latestPrimarySellingUnitPrice;

	@Override
	public String toString() {
		return name;
	}
}
