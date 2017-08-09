package ph.txtdis.service;

import ph.txtdis.dto.User;

import java.util.List;

public interface EdmsUserService {

	List<User> list();

	String getUsername(String name);
}
