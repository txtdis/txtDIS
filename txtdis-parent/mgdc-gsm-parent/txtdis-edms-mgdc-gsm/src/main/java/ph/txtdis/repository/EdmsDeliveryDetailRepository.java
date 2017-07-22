package ph.txtdis.repository;

import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsDeliveryDetail;

@Repository("edmsDeliveryDetailRepository")
public interface EdmsDeliveryDetailRepository //
		extends ReferencedRepository<EdmsDeliveryDetail, Short> {
}
