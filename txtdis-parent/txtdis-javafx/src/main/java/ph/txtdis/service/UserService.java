package ph.txtdis.service;

import ph.txtdis.dto.Role;
import ph.txtdis.dto.User;
import ph.txtdis.info.Information;
import ph.txtdis.type.UserType;

import java.util.List;

public interface UserService {

	User find(String username) throws Exception;

	User findByEmail(String email) throws Exception;

	List<Role> getRolesThatCanBeAssigned();

	String getSurname();

	void setSurname(String surname);

	String getUsername();

	boolean isEnabled();

	void setEnabled(boolean b);

	List<User> list() throws Exception;

	List<User> listByRole(UserType... types) throws Exception;

	List<String> listNamesByRole(UserType... types);

	void reset();

	void save(List<Role> roles) throws Information, Exception;

	User save(User entity) throws Exception;

	void setRoles(List<Role> items);

	void validateUsername(String username) throws Exception;
}