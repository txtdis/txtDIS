package ph.txtdis.repository;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface SpunRepository<T, PK extends Serializable> extends CrudRepository<T, PK> {

	T findFirstByIdGreaterThanOrderByIdAsc(@Param("id") PK id);

	T findFirstByIdLessThanOrderByIdDesc(@Param("id") PK id);

	T findFirstByOrderByIdAsc();

	T findFirstByOrderByIdDesc();
}
