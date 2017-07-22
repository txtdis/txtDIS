package ph.txtdis.dyvek.domain;

import static javax.persistence.CascadeType.ALL;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractCreatedKeyedEntity;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "billing", //
		indexes = { //
				@Index(name = "billing_customer_id_idx", columnList = "customer_id"), //
				@Index(name = "billing_delivery_id_idx", columnList = "delivery_id"), //
				@Index(name = "billing_item_id_idx", columnList = "item_id"), //
				@Index(name = "billing_order_date_idx", columnList = "order_date"), //
				@Index(name = "billing_order_id_idx", columnList = "order_id"), //
				@Index(name = "billing_order_no_idx", columnList = "order_no") })
public class BillableEntity //
		extends AbstractCreatedKeyedEntity<Long> {

	private static final long serialVersionUID = -4363805360652350591L;

	@ManyToOne
	private CustomerEntity customer;

	@Column(name = "order_no")
	private String orderNo;

	@Column(name = "order_date")
	private LocalDate orderDate;

	@ManyToOne
	private ItemEntity item;

	@Column(name = "qty")
	private BigDecimal totalQty;

	@OneToOne(cascade = ALL)
	@JoinColumn(name = "order_id")
	private OrderDetailEntity order;

	@OneToOne(cascade = ALL)
	@JoinColumn(name = "delivery_id")
	private DeliveryDetailEntity delivery;

	@OneToMany(mappedBy = "billing", cascade = ALL)
	private List<BillableReferenceEntity> bills;

	@OneToMany(mappedBy = "delivery", cascade = ALL)
	private List<BillableReferenceEntity> references;

	private String remarks;

	@OneToOne(mappedBy = "billing")
	private RemittanceDetailEntity payment;

	@Override
	public String toString() {
		return orderNo;
	}
}
