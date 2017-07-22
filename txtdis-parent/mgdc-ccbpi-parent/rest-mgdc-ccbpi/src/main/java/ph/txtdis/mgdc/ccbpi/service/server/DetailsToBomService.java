package ph.txtdis.mgdc.ccbpi.service.server;

import java.util.List;

import ph.txtdis.mgdc.ccbpi.domain.BomEntity;
import ph.txtdis.mgdc.ccbpi.domain.DetailedEntity;
import ph.txtdis.type.QuantityType;

public interface DetailsToBomService {

	List<BomEntity> toBomList(QuantityType type, List<? extends DetailedEntity> details);

	List<BomEntity> toEmptiesBomList(QuantityType type, List<? extends DetailedEntity> details);
}