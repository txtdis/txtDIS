package ph.txtdis.mgdc.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractCreatedKeyedEntity;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.time.LocalDate;

import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractRoutingEntity //
	extends AbstractCreatedKeyedEntity<Long> {

	private static final long serialVersionUID = -5435820150277484494L;

	@ManyToOne(optional = false)
	private RouteEntity route;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@Override
	public String toString() {
		return route + " starting " + toDateDisplay(startDate);
	}
}
