package ph.txtdis.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "item_id", "start_date", "type_id", "channel_limit_id" }) )
public class Price extends DecisionNeeded {

	private static final long serialVersionUID = -5721868302278165093L;

	@ManyToOne(optional = false)
	private PricingType type;

	@Column(name = "price", nullable = false)
	private BigDecimal priceValue;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@ManyToOne
	@JoinColumn(name = "channel_limit_id")
	private Channel channelLimit;
}
