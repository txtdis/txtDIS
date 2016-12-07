package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.User;

public interface UserService extends IdService<User, String> {

	List<User> list();

	String getUsername(String name);
}
