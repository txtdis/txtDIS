package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsSalesOrderDetail;

@Repository("edmsSalesOrderDetailRepository")
public interface EdmsSalesOrderDetailRepository extends CrudRepository<EdmsSalesOrderDetail, Short> {

	EdmsSalesOrderDetail findByReferenceNoAndItemCodeAndUomCode(@Param("referenceNo") String r,
			@Param("itemCode") String i, @Param("uom") String u);
}
