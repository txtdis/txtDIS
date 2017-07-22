package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.User;

public interface EdmsUserService {

	List<User> list();

	String getUsername(String name);
}
