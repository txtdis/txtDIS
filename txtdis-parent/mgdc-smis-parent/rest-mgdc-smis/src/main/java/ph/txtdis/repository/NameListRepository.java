package ph.txtdis.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface NameListRepository<T> extends CrudRepository<T, Long> {

	T findByName(@Param("name") String name);

	List<T> findByOrderByNameAsc();
}
