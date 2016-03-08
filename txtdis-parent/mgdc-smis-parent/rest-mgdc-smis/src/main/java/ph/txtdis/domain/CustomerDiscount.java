package ph.txtdis.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "customer_discount", //
		indexes = { @Index(columnList = "customer_id, start_date") }, //
		uniqueConstraints = //
		@UniqueConstraint(columnNames = { "customer_id", "start_date", "level", "family_limit_id" }) )
public class CustomerDiscount extends DecisionNeeded {

	private static final long serialVersionUID = -455882680349394952L;

	private int level;

	@Column(precision = 5, scale = 2)
	private BigDecimal percent;

	@ManyToOne
	@JoinColumn(name = "family_limit_id")
	private ItemFamily familyLimit;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;
}
