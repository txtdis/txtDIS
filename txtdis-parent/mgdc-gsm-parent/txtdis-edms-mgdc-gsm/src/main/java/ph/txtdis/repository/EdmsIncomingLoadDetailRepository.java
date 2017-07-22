package ph.txtdis.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsIncomingLoadDetail;

@Repository("edmsIncomingLoadDetailRepository")
public interface EdmsIncomingLoadDetailRepository //
		extends CrudRepository<EdmsIncomingLoadDetail, Short> {

	List<EdmsIncomingLoadDetail> findByReferenceNo( //
			@Param("referenceNo") String r);

	EdmsIncomingLoadDetail findByReferenceNoAndItemCode( //
			@Param("referenceNo") String r, //
			@Param("itemNo") String i);
}
