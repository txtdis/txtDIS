package ph.txtdis.mgdc.domain;

import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ph.txtdis.domain.AbstractCreatedKeyedEntity;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "holiday")
@EqualsAndHashCode(callSuper = true)
public class HolidayEntity extends AbstractCreatedKeyedEntity<Long> {

	private static final long serialVersionUID = 8041194797982340862L;

	@Column(nullable = false)
	protected String name;

	@Column(name = "declared_date", nullable = false, unique = true)
	private LocalDate declaredDate;

	@Override
	public String toString() {
		return toDateDisplay(declaredDate) + " - " + name;
	}
}
