package ph.txtdis.service;

import ph.txtdis.domain.EdmsSeller;
import ph.txtdis.dto.PickList;
import ph.txtdis.dto.User;

public interface HelperService //
	extends SavedService<User> {

	String getCode(PickList p);

	String getCode(EdmsSeller s);

	String getName(PickList p);
}
