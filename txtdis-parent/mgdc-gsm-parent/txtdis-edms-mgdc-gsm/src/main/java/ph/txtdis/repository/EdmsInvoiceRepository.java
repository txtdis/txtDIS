package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.domain.EdmsInvoice;

import java.time.LocalDate;
import java.util.List;

@Repository("edmsInvoiceRepository")
public interface EdmsInvoiceRepository //
	extends CrudRepository<EdmsInvoice, Long> {

	EdmsInvoice findByBookingNoAndStatus( //
	                                      @Param("bookingNo") String no, //
	                                      @Param("status") String closed);

	List<EdmsInvoice> findByOrderDateAndTruckCodeAndStatus( //
	                                                        @Param("orderDate") LocalDate d, //
	                                                        @Param("truckCode") String t, //
	                                                        @Param("status") String closed);

	EdmsInvoice findByReferenceNo( //
	                               @Param("referenceNo") String no);

	EdmsInvoice findByReferenceNoAndStatus( //
	                                        @Param("referenceNo") String no, //
	                                        @Param("closedOrVoid") String s);
}
