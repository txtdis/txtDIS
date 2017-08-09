package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.User;
import ph.txtdis.service.ServerUserService;

public interface UserService
	extends ServerUserService,
	Imported {

	void saveDriver(User u) throws Exception;

	void saveHelper(User u) throws Exception;
}
