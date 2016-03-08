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
import ph.txtdis.type.VolumeDiscountType;

@Data
@Entity
@Table(name = "volume_discount")
@EqualsAndHashCode(callSuper = true)
public class VolumeDiscount extends DecisionNeeded {

	private static final long serialVersionUID = 7710563453808768120L;

	@Column(nullable = false)
	private VolumeDiscountType type;

	@Column(nullable = false)
	private UomType uom;

	private int cutoff;

	@Column(nullable = false, precision = 8, scale = 4)
	private BigDecimal discount;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@ManyToOne
	@JoinColumn(name = "channel_limit")
	private Channel channelLimit;
}
