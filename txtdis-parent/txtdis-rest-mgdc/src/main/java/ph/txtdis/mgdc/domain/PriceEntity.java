package ph.txtdis.mgdc.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractDecisionNeededValidatedCreatedKeyedEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "price", //
	uniqueConstraints = @UniqueConstraint(columnNames = {"item_id", "start_date", "type_id"}))
public class PriceEntity
	extends AbstractDecisionNeededValidatedCreatedKeyedEntity<Long> {

	private static final long serialVersionUID = -5721868302278165093L;

	@ManyToOne(optional = false)
	private PricingTypeEntity type;

	@Column(name = "price", nullable = false)
	private BigDecimal priceValue;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@Override
	public String toString() {
		return "\n" + type + " - " + priceValue + " on " + startDate;
	}
}
