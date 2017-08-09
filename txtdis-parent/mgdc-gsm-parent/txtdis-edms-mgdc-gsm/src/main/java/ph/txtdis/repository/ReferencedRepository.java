package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import ph.txtdis.domain.Referenced;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface ReferencedRepository<T extends Referenced, PK extends Serializable> //
	extends CrudRepository<T, PK> {

	List<T> findAllByReferenceNo( //
	                              @Param("referenceNo") String r);
}
