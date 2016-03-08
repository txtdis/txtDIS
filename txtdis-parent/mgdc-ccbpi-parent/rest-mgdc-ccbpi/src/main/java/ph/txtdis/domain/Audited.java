package ph.txtdis.domain;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class Audited extends CreationTracked<Long> {

	private static final long serialVersionUID = 8939708088215882688L;

	@Column(name = "is_valid")
	private Boolean isValid;

	@Column(name = "audited_by")
	private String auditedBy;

	@Column(name = "audited_on")
	private ZonedDateTime auditedOn;
}