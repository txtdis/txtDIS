package ph.txtdis.repository;

import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsDistrict;

@Repository("edmsDistrictRepository")
public interface EdmsDistrictRepository extends CodeNameRepository<EdmsDistrict> {
}
