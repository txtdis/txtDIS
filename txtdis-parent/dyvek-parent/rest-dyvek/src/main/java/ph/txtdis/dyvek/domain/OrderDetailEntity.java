package ph.txtdis.dyvek.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractKeyedEntity;

@Data
@Entity
@Table(name = "order_detail", //
		indexes = { //
				@Index(name = "order_detail_closed_on_idx", columnList = "closed_on"), //
				@Index(name = "order_detail_end_date_idx", columnList = "end_date") })
@EqualsAndHashCode(callSuper = true)
public class OrderDetailEntity //
		extends AbstractKeyedEntity<Long> {

	private static final long serialVersionUID = -212968287448413279L;

	@Column(name = "tolerance")
	private BigDecimal tolerancePercent;

	@Column(name = "price")
	private BigDecimal priceValue;

	@Column(name = "end_date")
	private LocalDate endDate;

	@Column(name = "closed_by")
	private String closedBy;

	@Column(name = "closed_on")
	private ZonedDateTime closedOn;
}
