package ph.txtdis.service;

import ph.txtdis.domain.UserEntity;
import ph.txtdis.dto.User;
import ph.txtdis.type.UserType;

import java.util.List;

public interface ServerUserService //
	extends SavedReferencedKeyedService<UserEntity, User, String> {

	User findByEmail(String e);

	List<User> list();

	List<User> listNamesByRole(List<UserType> types);
}
