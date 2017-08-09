package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.domain.UserEntity;
import ph.txtdis.type.UserType;

import java.util.List;

@Repository("userRepository")
public interface UserRepository
	extends CrudRepository<UserEntity, String> {

	UserEntity findByEmail(@Param("email") String e);

	UserEntity findByMobile(@Param("mobile") String no);

	List<UserEntity> findByEnabledTrueAndRolesRoleInOrderByNameAsc(@Param("roles") List<UserType> l);

	List<UserEntity> findByEnabledTrueOrderByNameAsc();
}
