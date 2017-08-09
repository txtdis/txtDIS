package ph.txtdis.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.UserType;

import javax.persistence.*;

import static javax.persistence.CascadeType.ALL;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "authorities", //
	uniqueConstraints = @UniqueConstraint(name = "authorities_username_authority_key",
		columnNames = {"username", "authority"}))
public class AuthorityEntity //
	extends AbstractKeyedEntity<Long> {

	private static final long serialVersionUID = 4772261079413536002L;

	@ManyToOne(optional = false, cascade = ALL)
	@JoinColumn(name = "username")
	private UserEntity user;

	@Column(name = "authority", nullable = false)
	private UserType role;

	@Override
	public String toString() {
		return getUser() + " is a(n) " + getRole();
	}
}
