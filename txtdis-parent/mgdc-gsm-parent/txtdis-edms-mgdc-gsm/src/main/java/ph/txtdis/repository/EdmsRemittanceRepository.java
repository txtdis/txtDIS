package ph.txtdis.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsRemittance;

@Repository("edmsRemittanceRepository")
public interface EdmsRemittanceRepository extends CrudRepository<EdmsRemittance, Long> {

	List<EdmsRemittance> findByBillingNo(@Param("billingNo") String n);

	List<EdmsRemittance> findByAutoNo(@Param("autoNo") String n);

	Iterable<EdmsRemittance> findBySystemDate(LocalDate of);
}