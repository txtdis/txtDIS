package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class EdmsAbstractItemNameCostPriceUomQtyReferenceItemCodeIdDetail //
		extends EdmsAbstractCostPriceUomQtyReferenceItemCodeIdDetail {

	@Column(name = "itemName")
	private String itemName;
}
