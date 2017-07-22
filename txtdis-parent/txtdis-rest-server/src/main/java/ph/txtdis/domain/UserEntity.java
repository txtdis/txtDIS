package ph.txtdis.domain;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import ph.txtdis.dto.Keyed;

@Data
@Entity
@Table(name = "users")
public class UserEntity //
		implements Keyed<String>, Serializable {

	private static final long serialVersionUID = -2632553934643767369L;

	@Id
	@Column(name = "username")
	private String name;

	@Column(nullable = false)
	private String password;

	private String surname;

	@Column(nullable = false)
	private boolean enabled;

	@OneToMany(mappedBy = "user", cascade = ALL, fetch = EAGER)
	private List<AuthorityEntity> roles;

	@OneToOne(cascade = ALL)
	private StyleEntity style;

	private String email;

	private String mobile;

	public UserEntity disable() {
		setEnabled(false);
		return this;
	}

	@Override
	public String getId() {
		return getName();
	}

	@Override
	public void setId(String id) {
		setName(id);
	}

	@Override
	public String toString() {
		return getName();
	}
}
