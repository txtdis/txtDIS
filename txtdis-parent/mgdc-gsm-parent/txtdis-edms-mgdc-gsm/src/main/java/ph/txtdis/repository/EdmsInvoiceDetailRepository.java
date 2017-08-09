package ph.txtdis.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.domain.EdmsInvoiceDetail;

import java.util.List;

@Repository("emdsInvoiceDetailRepository")
public interface EdmsInvoiceDetailRepository //
	extends ReferencedRepository<EdmsInvoiceDetail, Long> {

	List<EdmsInvoiceDetail> findByReferenceNoAndSalesOrderDetailIdNotNull( //
	                                                                       @Param("referenceNo") String no);
}
