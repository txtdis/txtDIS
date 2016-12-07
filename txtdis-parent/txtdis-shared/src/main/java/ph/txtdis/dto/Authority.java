package ph.txtdis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.UserType;

@Data
@EqualsAndHashCode(callSuper = true)
public class Authority extends AbstractId<Long> {

	private UserType authority;

	@Override
	public String toString() {
		return authority.toString();
	}
}
