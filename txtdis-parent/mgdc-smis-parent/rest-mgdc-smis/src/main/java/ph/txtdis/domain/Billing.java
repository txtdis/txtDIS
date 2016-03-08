package ph.txtdis.domain;

import static java.lang.Math.abs;
import static javax.persistence.CascadeType.ALL;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(indexes = { //
		@Index(columnList = "customer_id, booking_id, is_rma"), //
		@Index(columnList = "customer_id, receiving_id, is_rma"), //
		@Index(columnList = "order_date, customer_id, is_rma, picking_id"), //
		@Index(columnList = "id_no, order_date, prefix, id_no, suffix"), //
		@Index(columnList = "id_no, is_fully_paid, customer_id, order_date"), //
		@Index(columnList = "id_no, is_rma, customer_id, is_fully_paid, order_date"), //
		@Index(columnList = "id_no, is_rma, customer_id, order_date, prefix, id_no, suffix"), //
		@Index(columnList = "id_no, is_rma, customer_id, booking_id, picking_id, order_date"), //
		@Index(columnList = "prefix, suffix, id_no"), //
		@Index(columnList = "printed_on, is_rma, is_valid, order_date, customer_id"), //
		@Index(columnList = "is_rma, booking_id, created_on"), //
		@Index(columnList = "is_rma, receiving_id, created_on"), //
		@Index(columnList = "booking_id, customer_id, created_on"), //
		@Index(columnList = "booking_id, customer_id, is_rma, created_on"), //
		@Index(columnList = "customer_id, receiving_id, created_on"), //
		@Index(columnList = "customer_id, is_rma, booking_id"), //
		@Index(columnList = "customer_id, is_rma, receiving_id"), //
		@Index(columnList = "id_no, created_on"), //
		@Index(columnList = "receiving_id") }, //
		uniqueConstraints = @UniqueConstraint(columnNames = { "prefix", "id_no", "suffix" }) )
public class Billing extends DecisionNeeded implements Detailed, Discounted {

	private static final long serialVersionUID = -4363805360652350591L;

	private String prefix, suffix;

	@Column(name = "id_no")
	private Long numId;

	@Column(name = "booking_id", unique = true)
	private Long bookingId;

	@Column(name = "receiving_id", unique = true)
	private Long receivingId;

	@ManyToOne
	private Picking picking;

	@ManyToOne
	private Customer customer;

	@OneToMany(mappedBy = "billing", cascade = ALL)
	private List<BillingDetail> details;

	@OneToMany(mappedBy = "billing", cascade = ALL)
	private List<RemittanceDetail> payments;

	@ManyToMany
	@JoinTable(name = "billing_customer_discount",
			joinColumns = { @JoinColumn(name = "billing_id", referencedColumnName = "id") },
			inverseJoinColumns = { @JoinColumn(name = "customer_discounts_id", referencedColumnName = "id") })
	private List<CustomerDiscount> customerDiscounts;

	@Column(name = "is_fully_paid")
	private Boolean fullyPaid;

	@Column(name = "is_rma")
	private Boolean rma;

	@Column(name = "unpaid", precision = 8, scale = 2)
	private BigDecimal unpaidValue;

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

	@Column(name = "printed_by")
	private String printedBy;

	@Column(name = "printed_on")
	private ZonedDateTime printedOn;

	@Column(name = "order_date")
	private LocalDate orderDate;

	@Column(name = "due_date")
	private LocalDate dueDate;

	@Column(name = "gross", precision = 8, scale = 2)
	private BigDecimal grossValue;

	@Column(name = "total", precision = 8, scale = 2)
	private BigDecimal totalValue;

	@Column(name = "bad_order_allowance", precision = 8, scale = 2)
	private BigDecimal badOrderAllowanceValue;
	
	@Column(name = "uploaded_by")
	private String uploadedBy;

	@Column(name = "uploaded_on")
	private ZonedDateTime uploadedOn;

	public String getOrderNo() {
		if (numId == null)
			return "";
		if (numId < 0)
			return "(" + abs(numId) + ")";
		return prefix() + numId + suffix();
	}

	private String prefix() {
		return prefix == null ? "" : prefix + "-";
	}

	private String suffix() {
		return suffix == null ? "" : suffix;
	}
}
