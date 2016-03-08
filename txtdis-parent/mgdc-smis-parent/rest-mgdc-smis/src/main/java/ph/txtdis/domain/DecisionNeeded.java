package ph.txtdis.domain;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class DecisionNeeded extends CreationTracked<Long> {

	private static final long serialVersionUID = -805511847282135281L;

	@Column(name = "is_valid")
	private Boolean isValid;

	private String remarks;

	@Column(name = "decided_by")
	private String decidedBy;

	@Column(name = "decided_on")
	private ZonedDateTime decidedOn;
}