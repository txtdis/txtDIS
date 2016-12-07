package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ph.txtdis.type.UserType;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "authorities", //
		uniqueConstraints = @UniqueConstraint(columnNames = { "username", "authority" }))

public class AuthorityEntity extends AbstractEntityId<Long> {

	private static final long serialVersionUID = 4772261079413536002L;

	@Column(nullable = false)
	private UserType authority;
}
