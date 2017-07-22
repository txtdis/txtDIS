package ph.txtdis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.UserType;

@Data
@EqualsAndHashCode(callSuper = true)
public class Authority //
		extends AbstractKeyed<Long> {

	private UserType authority;

	@Override
	public String toString() {
		return getAuthority().toString();
	}
}
