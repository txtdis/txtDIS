package ph.txtdis.service;

import ph.txtdis.domain.EdmsSeller;
import ph.txtdis.dto.PickList;
import ph.txtdis.dto.User;

public interface DriverService //
		extends SavedService<User> {

	String getCode(EdmsSeller s);

	String getCode(PickList p);

	String getName(PickList p);
}
