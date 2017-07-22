package ph.txtdis.mgdc.gsm.service.server;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.Bom;
import ph.txtdis.mgdc.gsm.domain.BomEntity;
import ph.txtdis.mgdc.gsm.domain.ItemEntity;
import ph.txtdis.mgdc.gsm.dto.Item;

public interface BomService {

	BomEntity createComponentOnly(ItemEntity part, BigDecimal qty);

	List<BomEntity> extractAll(ItemEntity i, BigDecimal qty);

	List<Bom> extractAll(Long itemId, String itemName, BigDecimal qty);

	List<Bom> getBadIncomingList(LocalDate start, LocalDate end);

	List<Bom> getBadOutgoingList(LocalDate start, LocalDate end);

	Bom getExpandedQtyInCases(Long id, String name, BigDecimal qty);

	List<Bom> getGoodIncomingList(LocalDate start, LocalDate end);

	List<Bom> getGoodOutgoingList(LocalDate start, LocalDate end);

	List<Bom> toBoms(List<BomEntity> l);

	List<BomEntity> toEntities(ItemEntity e, Item i);
}