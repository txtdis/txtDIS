package ph.txtdis.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.UomType;

@Data
@Entity
@Table(name = "customer_volume_discount")
@EqualsAndHashCode(callSuper = true)
public class CustomerVolumeDiscountEntity extends AbstractDecisionNeededEntity<Long> {

	private static final long serialVersionUID = -2078586370094283066L;

	@ManyToOne
	@JoinColumn(name = "item_id")
	private ItemEntity item;

	@Column(nullable = false)
	private UomType uom;

	@Column(name = "target_qty", nullable = false, precision = 12, scale = 4)
	private BigDecimal targetQty;

	@Column(name = "discount", nullable = false, precision = 12, scale = 4)
	private BigDecimal discountValue;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;
}
