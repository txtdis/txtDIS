package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.domain.EdmsDelivery;

@Repository("edmsDeliveryRepository")
public interface EdmsDeliveryRepository //
	extends CrudRepository<EdmsDelivery, Long> {

	EdmsDelivery findByBookingNoAndStatus( //
	                                       @Param("bookingNo") String b, //
	                                       @Param("status") String closed);
}
