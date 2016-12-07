package ph.txtdis.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsInvoiceDetail;

@Repository("emdsInvoiceDetailRepository")
public interface EdmsInvoiceDetailRepository extends JpaRepository<EdmsInvoiceDetail, Long> {

	List<EdmsInvoiceDetail> findByReferenceNoAndSalesOrderDetailIdNotNull(@Param("referenceNo") String r);
}
