package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.domain.AuthorityEntity;
import ph.txtdis.domain.UserEntity;
import ph.txtdis.type.UserType;

import java.util.List;

@Repository("roleRepository")
public interface RoleRepository
	extends CrudRepository<AuthorityEntity, Long> {

	List<AuthorityEntity> findByUser(@Param("user") UserEntity u);

	AuthorityEntity findByUserAndRole(@Param("user") UserEntity u, @Param("role") UserType r);
}
