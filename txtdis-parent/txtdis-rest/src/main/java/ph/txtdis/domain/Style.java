package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Style extends AbstractId<Long> {

	private static final long serialVersionUID = -4479575580426911105L;

	@Column(nullable = false)
	private String base;

	@Column(nullable = false)
	private String font;
}
