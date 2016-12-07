package ph.txtdis.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsSeller;

@Repository("edmsSellerRepository")
public interface EdmsSellerRepository extends CodeNameRepository<EdmsSeller> {

	EdmsSeller findByPlateNo(@Param("plateNo") String p);

	EdmsSeller findByTruckCode(@Param("truckCode") String t);
}
