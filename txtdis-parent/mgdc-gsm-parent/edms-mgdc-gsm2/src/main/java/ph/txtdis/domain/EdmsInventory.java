package ph.txtdis.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "master_item_uom")
@EqualsAndHashCode(callSuper = true)
public class EdmsInventory //
	extends EdmsAbstractInventory //
	implements Serializable {

	private static final long serialVersionUID = 2488947217819570470L;

	@Column(name = "mgdc2mainWarehouse")
	private BigDecimal goodQty;

	@Column(name = "mgdc2boWarehouse")
	private BigDecimal badQty;
}
