package ph.txtdis.dto;

import java.util.List;

import lombok.Data;

@Data
public class User implements Keyed<String> {

	private String username, password, surname, email;

	private Boolean enabled;

	private List<Authority> roles;

	private Style style;

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
