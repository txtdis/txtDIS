package ph.txtdis.mgdc.ccbpi.domain;

import static javax.persistence.CascadeType.ALL;
import static ph.txtdis.util.NumberUtils.toCurrencyText;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractCreatedKeyedEntity;
import ph.txtdis.dto.Booked;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "billing", //
	indexes = { //
		@Index(columnList = "booking_id, is_rma, billed_on"), //
		@Index(columnList = "receiving_id, is_rma"), //
		@Index(columnList = "order_date, is_rma, picking_id"), //
		@Index(columnList = "booking_id, created_on"), //
		@Index(columnList = "booking_id, is_rma, created_on"), //
		@Index(columnList = "receiving_id, created_on"), //
		@Index(columnList = "is_rma, booking_id, created_on"), //
		@Index(columnList = "is_rma, receiving_id, created_on"), //
		@Index(columnList = "due_date, received_on"), //
		@Index(columnList = "order_date, received_on")})
public class BillableEntity //
	extends AbstractCreatedKeyedEntity<Long> //
	implements Booked,
	DetailedEntity {

	private static final long serialVersionUID = -4363805360652350591L;

	private String prefix, suffix;

	@Column(name = "due_date")
	private LocalDate dueDate;

	@Column(name = "booking_id")
	private Long bookingId;

	@Column(name = "receiving_id", unique = true)
	private Long receivingId;

	@ManyToOne(cascade = ALL)
	private PickListEntity picking;

	@ManyToOne
	private CustomerEntity customer;

	@OneToMany(mappedBy = "billing", cascade = ALL)
	private List<BillableDetailEntity> details;

	@Column(name = "order_date")
	private LocalDate orderDate;

	@Column(name = "total")
	private BigDecimal totalValue;

	@Column(name = "is_rma")
	private Boolean rma;

	private String remarks;

	@Column(name = "billed_by")
	private String billedBy;

	@Column(name = "billed_on")
	private ZonedDateTime billedOn;

	@Column(name = "received_by")
	private String receivedBy;

	@Column(name = "received_on")
	private ZonedDateTime receivedOn;

	@Column(name = "receiving_modified_by")
	private String receivingModifiedBy;

	@Column(name = "receiving_modified_on")
	private ZonedDateTime receivingModifiedOn;

	public String getOrderNo() {
		return prefix() + suffix();
	}

	private String prefix() {
		return prefix == null ? "" : prefix + "-";
	}

	private String suffix() {
		return suffix == null ? "" : suffix;
	}

	@Override
	public String toString() {
		return "\nBill No. " + getId() + ": " + getOrderDate() + ", " + customer + ", " + toCurrencyText(totalValue);
	}
}
