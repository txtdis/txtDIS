package ph.txtdis.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "master_item_uom")
@EqualsAndHashCode(callSuper = true)
public class EdmsInventory extends EdmsAbstractInventory implements Serializable {

	private static final long serialVersionUID = -7812225856101060992L;

	@Column(name = "mgdc3mainWarehouse")
	private BigDecimal goodQty;

	@Column(name = "mgdc3boWarehouse")
	private BigDecimal badQty;
}
