package ph.txtdis.domain;

import lombok.Data;
import ph.txtdis.dto.Keyed;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

@Data
@Entity
@Table(name = "users")
public class UserEntity //
	implements Keyed<String>,
	Serializable {

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
