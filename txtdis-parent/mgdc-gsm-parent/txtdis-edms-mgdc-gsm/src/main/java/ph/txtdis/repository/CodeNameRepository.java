package ph.txtdis.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface CodeNameRepository<T> extends NameListRepository<T> {

	T findByCode(@Param("code") String c);

	T findByNameContainingIgnoreCase(@Param("name") String n);
}
