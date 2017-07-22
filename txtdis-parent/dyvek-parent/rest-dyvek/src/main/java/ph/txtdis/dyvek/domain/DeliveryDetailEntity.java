package ph.txtdis.dyvek.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractKeyedEntity;

@Data
@Entity
@Table(name = "delivery_detail", //
		indexes = { //
				@Index(name = "delivery_detail_assigned_to_purchase_on_idx", columnList = "assigned_to_purchase_on"), //
				@Index(name = "delivery_detail_assigned_to_sales_on_idx", columnList = "assigned_to_sales_on"), //
				@Index(name = "delivery_detail_recipient_id_idx", columnList = "recipient_id") })
@EqualsAndHashCode(callSuper = true)
public class DeliveryDetailEntity //
		extends AbstractKeyedEntity<Long> {

	private static final long serialVersionUID = 1759960406252010860L;

	@ManyToOne
	private CustomerEntity recipient;

	@Column(name = "plate_no", nullable = false)
	private String plateNo;

	@Column(name = "scale_no", nullable = false)
	private String scaleNo;

	private String color;

	@Column(precision = 5, scale = 2)
	private BigDecimal iodine, lauric, oleic;

	@Column(precision = 7, scale = 4)
	private BigDecimal moisture, saponification;

	@Column(name = "bill_adjustment_qty")
	private BigDecimal billAdjustmentQty;

	@Column(name = "bill_adjustment_price")
	private BigDecimal billAdjustmentPriceValue;

	@Column(name = "bill_adjusted_by")
	private String billAdjustedBy;

	@Column(name = "bill_adjusted_on")
	private ZonedDateTime billAdjustedOn;

	@Column(name = "soa_adjustment_qty")
	private BigDecimal soaAdjustmentQty;

	@Column(name = "soa_adjustment_price")
	private BigDecimal soaAdjustmentPriceValue;

	@Column(name = "assigned_to_purchase_by")
	private String assignedToPurchaseBy;

	@Column(name = "assigned_to_purchase_on")
	private ZonedDateTime assignedToPurchaseOn;

	@Column(name = "assigned_to_sales_by")
	private String assignedToSalesBy;

	@Column(name = "assigned_to_sales_on")
	private ZonedDateTime assignedToSalesOn;

	@Column(name = "soa_no")
	private String soaNo;

	@Column(name = "soa_date")
	private LocalDate soaDate;

	@Column(name = "soa_received_by")
	private String soaReceivedBy;

	@Column(name = "soa_received_on")
	private ZonedDateTime soaReceivedOn;
}
