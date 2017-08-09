package ph.txtdis.mgdc.ccbpi.domain;

import static ph.txtdis.util.NumberUtils.toCurrencyText;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.mgdc.domain.AbstractCustomerDiscountEntity;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "customer_discount", //
	indexes = {@Index(columnList = "customer_id, start_date")}, //
	uniqueConstraints = {@UniqueConstraint(columnNames = {"customer_id", "start_date"})})
public class CustomerDiscountEntity //
	extends AbstractCustomerDiscountEntity {

	private static final long serialVersionUID = -455882680349394952L;

	@ManyToOne(optional = false)
	private CustomerEntity customer;

	@Override
	public String toString() {
		return getCustomer() + ": " + toCurrencyText(getValue());
	}
}
