package ph.txtdis.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.UserEntity;
import ph.txtdis.type.UserType;

@Repository("userRepository")
public interface UserRepository extends CrudRepository<UserEntity, String> {

	UserEntity findByEmail(@Param("email") String e);

	UserEntity findByMobile(@Param("mobile") String no);

	List<UserEntity> findByEnabledTrueAndRolesAuthorityInOrderByUsernameAsc(@Param("roles") List<UserType> l);

	List<UserEntity> findByEnabledTrueOrderByUsernameAsc();
}
