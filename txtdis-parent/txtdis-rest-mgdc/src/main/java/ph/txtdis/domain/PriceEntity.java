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
@Table(name = "price", //
		uniqueConstraints = @UniqueConstraint(columnNames = { "item_id", "start_date", "type_id", "channel_limit_id" }))
public class PriceEntity extends AbstractDecisionNeededEntity<Long> {

	private static final long serialVersionUID = -5721868302278165093L;

	@ManyToOne(optional = false)
	private PricingTypeEntity type;

	@Column(name = "price", nullable = false)
	private BigDecimal priceValue;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@ManyToOne
	@JoinColumn(name = "channel_limit_id")
	private ChannelEntity channelLimit;

	@Override
	public String toString() {
		return type + " - " + priceValue + " on " + startDate + " for " + (channelLimit == null ? "ALL" : channelLimit);
	}
}
