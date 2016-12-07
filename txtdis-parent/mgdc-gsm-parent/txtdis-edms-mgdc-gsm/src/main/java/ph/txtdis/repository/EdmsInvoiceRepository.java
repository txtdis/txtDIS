package ph.txtdis.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsInvoice;

@Repository("edmsInvoiceRepository")
public interface EdmsInvoiceRepository extends CrudRepository<EdmsInvoice, Long> {

	List<EdmsInvoice> findByReferenceNo(@Param("referenceNo") String no);

	EdmsInvoice findByReferenceNoAndStatus(@Param("referenceNo") String no, @Param("status") String closedOrVoid);

	List<EdmsInvoice> findByOrderDateAndTruckCodeAndStatus(@Param("orderDate") LocalDate d, @Param("truckCode") String t,
			@Param("status") String closed);
}
