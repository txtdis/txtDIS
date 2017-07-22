package ph.txtdis.mgdc.gsm.service.server;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.mgdc.gsm.domain.BomEntity;

public interface ReceivingService {

	List<BomEntity> listBadItemsIncomingQty(LocalDate start, LocalDate end);

	List<BomEntity> listBadItemsOutgoingQty(LocalDate start, LocalDate end);

	List<BomEntity> listGoodItemsIncomingQty(LocalDate start, LocalDate end);

	List<BomEntity> listGoodItemsOutgoingQty(LocalDate start, LocalDate end);
}
