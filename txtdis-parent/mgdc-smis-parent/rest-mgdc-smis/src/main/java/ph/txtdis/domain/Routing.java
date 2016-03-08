package ph.txtdis.domain;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "customer_id", "start_date" }) )
public class Routing extends CreationTracked<Long> {

	private static final long serialVersionUID = -4540897080828317375L;

	@ManyToOne(optional = false)
	private Route route;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;
}
