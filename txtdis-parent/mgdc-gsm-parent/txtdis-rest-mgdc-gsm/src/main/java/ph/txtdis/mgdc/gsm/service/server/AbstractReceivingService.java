package ph.txtdis.mgdc.gsm.service.server;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.domain.BomEntity;
import ph.txtdis.type.TransactionDirectionType;

public abstract class AbstractReceivingService //
		extends AbstractSpunSavedBillableService // 
		implements ReceivableService, ReceivingService, QtyPerItemService {

	@Override
	protected BillableEntity create(Billable b) {
		BillableEntity e = super.create(b);
		return setAllReceivingData(e, b);
	}

	private BillableEntity setAllReceivingData(BillableEntity e, Billable b) {
		e = setReceivingData(e, b, repository);
		return setModifiedReceivingData(e, b);
	}

	private BillableEntity setModifiedReceivingData(BillableEntity e, Billable b) {
		if (b.getReceivingModifiedBy() == null || e.getReceivingModifiedBy() != null)
			return e;
		e.setReceivingModifiedBy(b.getReceivingModifiedBy());
		e.setReceivingModifiedOn(nowIfReceivingModifiedOnNull(b));
		return e;
	}

	private ZonedDateTime nowIfReceivingModifiedOnNull(Billable b) {
		ZonedDateTime t = b.getReceivingModifiedOn();
		return t != null ? t : ZonedDateTime.now();
	}

	@Override
	public List<BomEntity> listBadItemsIncomingQty(LocalDate start, LocalDate end) {
		List<BillableEntity> l = listWithReceiptEntities(start, end);
		if (l == null)
			return null;
		l = l.stream().filter(e -> e.getRma() != null && e.getRma() == false).collect(Collectors.toList());
		return toBomList(TransactionDirectionType.INCOMING, l);
	}

	private List<BillableEntity> listWithReceiptEntities(LocalDate start, LocalDate end) {
		return repository.findByOrderDateBetweenAndReceivedOnNotNull(start, end);
	}

	@Override
	public List<BomEntity> listBadItemsOutgoingQty(LocalDate start, LocalDate end) {
		List<BillableEntity> l = listPicked(start, end);
		if (l == null)
			return null;
		l = l.stream().filter(e -> e.getRma() != null && e.getRma() == false).collect(Collectors.toList());
		return toBomList(TransactionDirectionType.OUTGOING, l);
	}

	private List<BillableEntity> listPicked(LocalDate start, LocalDate end) {
		return repository.findByOrderDateBetweenAndPickingNotNull(start, end);
	}

	@Override
	public List<BomEntity> listGoodItemsIncomingQty(LocalDate start, LocalDate end) {
		List<BillableEntity> l = listWithReceiptEntities(start, end);
		if (l == null)
			return null;
		l = l.stream().filter(e -> e.getRma() == null || e.getRma() == true).collect(Collectors.toList());
		return toBomList(TransactionDirectionType.INCOMING, l);
	}

	@Override
	public List<BomEntity> listGoodItemsOutgoingQty(LocalDate start, LocalDate end) {
		List<BillableEntity> l = listPicked(start, end);
		if (l == null)
			return null;
		l = l.stream().filter(e -> e.getRma() == null || e.getRma() == true).collect(Collectors.toList());
		return toBomList(TransactionDirectionType.OUTGOING, l);
	}
}