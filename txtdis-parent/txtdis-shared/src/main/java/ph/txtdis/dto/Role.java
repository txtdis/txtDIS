package ph.txtdis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.UserType;

@Data
@EqualsAndHashCode(callSuper = true)
public class Role //
		extends AbstractKeyed<Long> {

	private boolean isEnabled;

	private UserType authority;

	public Boolean getEnabled() {
		return isEnabled;
	}

	public String getRole() {
		return authority == null ? "" : authority.toString().replace("_", " ");
	}
}
