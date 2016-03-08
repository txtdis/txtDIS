package ph.txtdis.domain;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class NumberedOrder extends CreationTracked<Long> {

	private static final long serialVersionUID = 2042906262421586568L;

	@ManyToOne(optional = false)
	protected Customer customer;

	@Column(name = "order_date", nullable = false)
	protected LocalDate orderDate;

	private String remarks;
}
