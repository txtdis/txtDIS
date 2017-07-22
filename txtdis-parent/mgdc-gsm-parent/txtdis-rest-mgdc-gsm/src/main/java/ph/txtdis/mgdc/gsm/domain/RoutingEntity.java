package ph.txtdis.mgdc.gsm.domain;

import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.mgdc.domain.AbstractRoutingEntity;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "routing", //
		uniqueConstraints = @UniqueConstraint(columnNames = { "customer_id", "start_date" }))
public class RoutingEntity //
		extends AbstractRoutingEntity {

	private static final long serialVersionUID = -4540897080828317375L;

	@ManyToOne(optional = false)
	private CustomerEntity customer;

	@Override
	public String toString() {
		return getRoute() + " of " + getCustomer() + " starting " + toDateDisplay(getStartDate());
	}
}
