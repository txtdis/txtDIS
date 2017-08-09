package ph.txtdis.mgdc.gsm.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.mgdc.domain.AbstractCustomerDiscountEntity;

import javax.persistence.*;

import static ph.txtdis.util.NumberUtils.toCurrencyText;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "customer_discount", //
	indexes = {@Index(columnList = "customer_id, start_date")}, //
	uniqueConstraints = {@UniqueConstraint(columnNames = {"customer_id", "start_date", "item_id"})})
public class CustomerDiscountEntity //
	extends AbstractCustomerDiscountEntity {

	private static final long serialVersionUID = -455882680349394952L;

	@ManyToOne(optional = false)
	private CustomerEntity customer;

	@ManyToOne
	@JoinColumn(name = "item_id")
	private ItemEntity item;

	@Override
	public String toString() {
		return getCustomer() + ": item=" + getItem() + " discount= " + toCurrencyText(getValue());
	}
}
