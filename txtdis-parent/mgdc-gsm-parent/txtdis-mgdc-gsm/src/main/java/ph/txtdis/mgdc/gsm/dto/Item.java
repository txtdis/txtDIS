package ph.txtdis.mgdc.gsm.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.*;
import ph.txtdis.type.ItemType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class Item //
	extends AbstractModificationTracked<Long> {

	private boolean notDiscounted;

	private String description, name, vendorNo;

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
