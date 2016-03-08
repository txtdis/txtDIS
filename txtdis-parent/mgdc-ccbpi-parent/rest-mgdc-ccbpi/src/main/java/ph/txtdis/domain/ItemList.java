package ph.txtdis.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "item_list")
@EqualsAndHashCode(callSuper = true)
public class ItemList extends AbstractId<Long> {

	private static final long serialVersionUID = 257754573072417395L;

	@ManyToOne(optional = false)
	private Item item;

	@Column(name = "price")
	private BigDecimal priceValue;

	@Column(name = "initial")
	private Integer initialCount;

	@Column(name = "returned")
	private Integer returnedCount;
}
