package ph.txtdis.service;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.domain.BillableDetailEntity;
import ph.txtdis.domain.BomEntity;
import ph.txtdis.dto.Bom;

public interface BomService {

	List<Bom> getBadIncomingList(LocalDate start, LocalDate end);

	List<Bom> getBadOutgoingList(LocalDate start, LocalDate end);

	List<Bom> getGoodIncomingList(LocalDate start, LocalDate end);

	List<Bom> getGoodOutgoingList(LocalDate start, LocalDate end);

	List<BomEntity> listBomEntities(BillableDetailEntity d);

	List<BomEntity> toEntities(List<Bom> l);

	List<Bom> toList(List<BomEntity> l);
}