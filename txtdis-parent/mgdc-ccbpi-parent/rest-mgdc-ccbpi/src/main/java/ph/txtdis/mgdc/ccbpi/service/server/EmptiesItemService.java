package ph.txtdis.mgdc.ccbpi.service.server;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import ph.txtdis.mgdc.ccbpi.domain.BomEntity;
import ph.txtdis.mgdc.ccbpi.domain.ItemEntity;
import ph.txtdis.mgdc.ccbpi.dto.Item;
import ph.txtdis.type.PriceType;

public interface EmptiesItemService
	extends ItemService {

	List<Item> listEmpties();

	Map<ItemEntity, BigDecimal> mapEmptiesPriceValue(PriceType type, List<BomEntity> boms);
}
