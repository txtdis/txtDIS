package ph.txtdis.domain;

import static java.util.Collections.emptyList;
import static javax.persistence.CascadeType.ALL;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "sales_order")
public class SalesOrder extends AbstractEntityId<Long> {

	private static final long serialVersionUID = -7474940802227888900L;
	
	@Column(name = "order_date", nullable = false)
	private LocalDate orderDate;

	@ManyToOne(optional = false)
	private CustomerImpl customer;
	
	@ManyToOne
	@JoinColumn(name = "sent_log_id")
	private SmsLog sentLog;
	
	@ManyToOne
	@JoinColumn(name = "approval_log_id")
	private SmsLog approvalLog;
	
	@OneToMany(cascade = ALL)
	@JoinColumn(name = "sales_order_id")
	private List<SalesOrderItem> details;

	public SalesOrder(LocalDate orderDate, CustomerImpl customer) {
		this.orderDate = orderDate;
		this.customer = customer;
	}
	
	public List<SalesOrderItem> getDetails() {
		return details != null ? details : emptyList();
	}
}
