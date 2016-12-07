package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.ItemEntity;
import ph.txtdis.domain.QtyPerUomEntity;
import ph.txtdis.type.UomType;

@Repository("qtyPerUomRepository")
public interface QtyPerUomRepository extends CrudRepository<QtyPerUomEntity, Long> {

	QtyPerUomEntity findByItemAndUom(@Param("item") ItemEntity i, @Param("uom") UomType u);
}
