package ph.txtdis.mgdc.gsm.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.mgdc.gsm.domain.ItemEntity;
import ph.txtdis.mgdc.gsm.domain.QtyPerUomEntity;
import ph.txtdis.type.UomType;

@Repository("qtyPerUomRepository")
public interface QtyPerUomRepository //
	extends CrudRepository<QtyPerUomEntity, Long> {

	QtyPerUomEntity findByItemAndUom( //
	                                  @Param("item") ItemEntity i, //
	                                  @Param("uom") UomType u);
}
