package ph.txtdis.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface NameListRepository<T> extends CrudRepository<T, Long> {

	T findByCode(@Param("code") Long c);

	T findByName(@Param("name") String n);

	List<T> findByOrderByNameAsc();
}
