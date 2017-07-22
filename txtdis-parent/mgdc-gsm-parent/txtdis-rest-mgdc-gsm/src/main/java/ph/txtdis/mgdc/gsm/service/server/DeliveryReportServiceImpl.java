package ph.txtdis.mgdc.gsm.service.server;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Bom;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.repository.DeliveryReportRepository;

@Service("deliveryReportService")
public class DeliveryReportServiceImpl //
		extends AbstractSpunSavedBillingService //
		implements DeliveryReportService {

	@Autowired
	private DeliveryReportRepository deliveryReportRepository;

	@Autowired
	private BomService bomService;

	@Override
	protected BillableEntity create(Billable b) {
		BillableEntity e = super.create(b);
		return updateLoadOrderQtyAndNegateBookingIdIfFromLoadOrderThenSetBillingData(repository, e, b);
	}

	@Override
	public List<Bom> extractAll(Long itemId, String itemName, BigDecimal qty) {
		return bomService.extractAll(itemId, itemName, qty);
	}

	@Override
	protected BillableEntity firstEntity() {
		return deliveryReportRepository.findFirstByNumIdLessThanOrderByIdAsc(0L);
	}

	@Override
	protected BillableEntity lastEntity() {
		return deliveryReportRepository.findFirstByNumIdLessThanOrderByIdDesc(0L);
	}

	@Override
	protected BillableEntity nextEntity(Long id) {
		return deliveryReportRepository.findFirstByNumIdLessThanAndIdGreaterThanOrderByIdAsc(0L, id);
	}

	@Override
	public Long numId(Billable b) {
		return b.getNumId() == null && b.getBilledBy() != null ? deliveryId() : b.getNumId();
	}

	@Override
	protected BillableEntity previousEntity(Long id) {
		return deliveryReportRepository.findFirstByNumIdLessThanAndIdLessThanOrderByIdDesc(0L, id);
	}

	@Override
	protected BillableEntity update(BillableEntity e, Billable b) {
		e = super.update(e, b);
		return updateLoadOrderQtyAndNegateBookingIdIfFromLoadOrderThenSetBillingData(repository, e, b);
	}
}