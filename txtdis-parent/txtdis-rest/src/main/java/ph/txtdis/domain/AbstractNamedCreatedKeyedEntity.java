package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = false)
public class AbstractNamedCreatedKeyedEntity<PK> //
		extends AbstractCreatedKeyedEntity<PK> {

	private static final long serialVersionUID = -5859599323217964344L;

	@Column(nullable = false, unique = true)
	private String name;
}
