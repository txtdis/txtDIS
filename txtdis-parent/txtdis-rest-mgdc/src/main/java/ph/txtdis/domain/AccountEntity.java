package ph.txtdis.domain;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "account", //
		uniqueConstraints = @UniqueConstraint(columnNames = { "route_id", "start_date" }))
public class AccountEntity extends AbstractCreatedEntity<Long> {

	private static final long serialVersionUID = -3816774251745575218L;

	@Column(nullable = false)
	private String seller;

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;
}
