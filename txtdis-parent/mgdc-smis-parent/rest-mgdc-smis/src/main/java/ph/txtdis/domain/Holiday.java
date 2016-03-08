package ph.txtdis.domain;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;

import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Holiday extends CreationTracked<Long> {

	private static final long serialVersionUID = 8041194797982340862L;

	@Column(name = "declared_date", nullable = false, unique = true)
	private LocalDate declaredDate;

	@Column(nullable = false)
	protected String name;

	@Override
	public String toString() {
		return toDateDisplay(declaredDate) + " - " + name;
	}
}
