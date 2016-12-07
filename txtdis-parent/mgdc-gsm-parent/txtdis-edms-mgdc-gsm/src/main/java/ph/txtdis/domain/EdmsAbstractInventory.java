package ph.txtdis.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class EdmsAbstractInventory extends EdmsAbstractId {

	@Column(name = "itemCode")
	private String itemCode;

	@Column(name = "uomCode")
	private String uomCode;

	@Column(name = "qty")
	private BigDecimal qty;

	@Column(name = "cost")
	private BigDecimal costValue;

	@Column(name = "price")
	private BigDecimal priceValue;

	@Column(name = "uomLevel")
	private String uomLevelCode;
}
