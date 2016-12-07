package ph.txtdis.domain;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;
import ph.txtdis.dto.Keyed;

@Data
@Entity
@Table(name = "users")
public class UserEntity implements Keyed<String>, Serializable {

	private static final long serialVersionUID = -2632553934643767369L;

	@Id
	private String username;

	@Column(nullable = false)
	private String password;

	private String surname;

	@Column(nullable = false)
	private boolean enabled;

	@JoinColumn(name = "username")
	@OneToMany(cascade = ALL, fetch = EAGER)
	private List<AuthorityEntity> roles;

	@OneToOne(cascade = ALL)
	private StyleEntity style;

	private String email;

	private String mobile;

	public UserEntity disable() {
		enabled = false;
		return this;
	}

	@Override
	public String getId() {
		return username;
	}

	@Override
	public void setId(String id) {
		username = id;
	}

	@Override
	public String toString() {
		return username;
	}
}
