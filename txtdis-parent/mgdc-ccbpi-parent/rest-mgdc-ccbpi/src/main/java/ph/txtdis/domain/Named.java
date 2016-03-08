package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = false)
public class Named<PK> extends CreationTracked<PK> {

	private static final long serialVersionUID = -5859599323217964344L;

	@Column(unique = true)
	protected Long code;

	@Column(nullable = false)
	protected String name;
}
