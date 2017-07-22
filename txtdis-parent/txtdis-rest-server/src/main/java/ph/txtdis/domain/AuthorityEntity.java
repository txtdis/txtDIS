package ph.txtdis.domain;

import static javax.persistence.CascadeType.ALL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.UserType;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "authorities", //
		uniqueConstraints = @UniqueConstraint(name = "authorities_username_authority_key", columnNames = { "username", "authority" }))
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
