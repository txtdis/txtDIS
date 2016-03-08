package ph.txtdis.domain;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "route_id", "start_date" }) )
public class Account extends CreationTracked<Long> {

	private static final long serialVersionUID = -3816774251745575218L;

	@Column(nullable = false)
	private String seller;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;
}
