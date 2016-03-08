package ph.txtdis.domain;

import static javax.persistence.CascadeType.ALL;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Billing extends Audited implements Detailed, Keyed<Long> {

	private static final long serialVersionUID = -4363805360652350591L;

	@OneToMany(cascade = ALL)
	@JoinColumn(name = "billing_id")
	private List<ItemList> details;

	@ManyToOne(optional = false)
	private Customer customer;

	@Column(name = "printed_by")
	private String printedBy;

	@Column(name = "printed_on")
	private ZonedDateTime printedOn;

	@Column(name = "receiving_id", unique = true)
	private Long receivingId;

	@Column(name = "received_by")
	private String receivedBy;

	@Column(name = "received_on")
	private ZonedDateTime receivedOn;

	@Column(name = "order_date")
	private LocalDate orderDate;

	@Column(name = "total_quantity")
	private BigDecimal totalQty;

	@Column(name = "total_amount", precision = 8, scale = 2)
	private BigDecimal totalValue;

	@Column(name = "unpaid", precision = 8, scale = 2)
	private BigDecimal unpaidValue;

	private String collector;
}
