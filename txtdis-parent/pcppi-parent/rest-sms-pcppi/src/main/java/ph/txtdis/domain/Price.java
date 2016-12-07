package ph.txtdis.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
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
@Table(indexes = { //
		@Index(columnList = "start_date") }, //
		uniqueConstraints = @UniqueConstraint(columnNames = { "item_id", "start_date" }) )
public class Price extends AbstractEntityId<Long> implements Comparable<Price> {

	private static final long serialVersionUID = -5721868302278165093L;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@Column(name = "price", nullable = false)
	private BigDecimal priceValue;

	@Override
	public int compareTo(Price p) {
		return p.getStartDate().compareTo(startDate);
	}
}
