package ph.txtdis.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "customer_sales_volume", //
indexes = { //
		@Index(columnList = "customer_id, item_id") }, //
		uniqueConstraints = @UniqueConstraint(columnNames = { "customer_id", "item_id" }) )
public class CustomerSalesVolume extends AbstractEntityId<Long> {

	private static final long serialVersionUID = -7474940802227888900L;

	@ManyToOne(optional = false)
	private CustomerImpl customer;

	@ManyToOne(optional = false)
	private Item item;

	@Column(name = "avg_daily_qty", nullable = false, precision = 8, scale = 4)
	private BigDecimal avgDailyQty;
}
