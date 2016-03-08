package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.UserType;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "authorities", //
		uniqueConstraints = @UniqueConstraint(columnNames = { "username", "authority" }) )

public class Authority extends AbstractId<Long> {

	private static final long serialVersionUID = 4772261079413536002L;

	@Column(nullable = false)
	private UserType authority;
}
