package ph.txtdis.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "sales_order_item")
public class SalesOrderItem extends AbstractId<Long> {

	private static final long serialVersionUID = -6178110363985848836L;

	@ManyToOne(optional = false)
	private Item item;

	private int qty;
}
