package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.domain.EdmsBusinessAddress;

import java.util.List;

@Repository("edmsBusinessAddressRepository")
public interface EdmsBusinessAddressRepository //
	extends CrudRepository<EdmsBusinessAddress, Long> {

	List<EdmsBusinessAddress> findByProvinceAndCityContainingAllIgnoreCase( //
	                                                                        @Param("province") String p, //
	                                                                        @Param("city") String c);

	EdmsBusinessAddress findFirstByProvinceAndCityAndBarangayContainingAllIgnoreCase( //
	                                                                                  @Param("province") String p, //
	                                                                                  @Param("city") String c, //
	                                                                                  @Param("barangay") String b);

	EdmsBusinessAddress findFirstByProvinceContainingIgnoreCase( //
	                                                             @Param("province") String p);
}
