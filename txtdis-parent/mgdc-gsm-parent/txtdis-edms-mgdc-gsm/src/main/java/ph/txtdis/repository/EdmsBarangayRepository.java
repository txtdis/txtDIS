package ph.txtdis.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsBusinessAddress;

@Repository("edmsBarangayRepository")
public interface EdmsBarangayRepository extends CrudRepository<EdmsBusinessAddress, Long> {

	List<EdmsBusinessAddress> findByProvinceAndCityContainingAllIgnoreCase(@Param("province") String p,
			@Param("city") String c);

	EdmsBusinessAddress findByCityAndBarangayAllIgnoreCase(String city, String barangay);
}
