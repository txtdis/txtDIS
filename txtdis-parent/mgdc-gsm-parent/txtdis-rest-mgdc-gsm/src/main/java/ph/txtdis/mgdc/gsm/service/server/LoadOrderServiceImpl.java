package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.stereotype.Service;
import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.gsm.domain.BillableDetailEntity;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.repository.LoadOrderRepository;
import ph.txtdis.util.NumberUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static ph.txtdis.type.PartnerType.EX_TRUCK;

@Service("loadOrderService")
public class LoadOrderServiceImpl //
	extends AbstractBookingService<LoadOrderRepository> //
	implements LoadOrderService {

	@Override
	public Billable findAsReference(Long id) {
		BillableEntity e =
			bookingRepository.findFirstByCustomerTypeInAndBookingIdAndRmaNullOrderById(DELIVERED_ROUTES, id);
		return toModel(e);
	}

	@Override
	public List<Billable> findBooked(LocalDate d) {
		List<BillableEntity> l = bookingRepository.findByOrderDateAndCustomerType(d, EX_TRUCK);
		return l == null ? null : //
			l.stream().map(e -> customerOnlyBillable(null, e))//
				.collect(Collectors.toList());
	}

	@Override
	public Billable find(LocalDate d, String exTruck) {
		BillableEntity b = bookingRepository.findByCustomerNameAndOrderDate(exTruck, d);
		return toModel(b);
	}

	@Override
	public Billable findWithLoadVariance(LocalDate date) {
		List<BillableEntity> l = bookingRepository.findByCustomerTypeAndPickingNotNullAndOrderDateBetween( //
			EX_TRUCK, goLive(), billingCutoff(date));
		BillableEntity e = l == null ? null
			: l.stream() //
			.flatMap(b -> b.getDetails().stream()) //
			.filter(d -> isShort(d)) //
			.map(d -> d.getBilling()).distinct() //
			.findFirst().orElse(null);
		return toBookingIdOnlyBillable(e);
	}

	private boolean isShort(BillableDetailEntity d) {
		return NumberUtils.isPositive(d.getFinalQty());
	}

	@Override
	public Billable findShort(Long id) {
		BillableEntity e = findEntityByBookingNo(id.toString());
		return isShort(e) ? toBookingIdOnlyBillable(e) : null;
	}

	@Override
	public BillableEntity findEntityByBookingNo(String key) {
		return bookingRepository.findByCustomerTypeAndBookingId(EX_TRUCK, Long.valueOf(key));
	}

	private boolean isShort(BillableEntity e) {
		return e == null ? false : e.getDetails().stream().anyMatch(d -> isShort(d));
	}

	@Override
	public Billable findUnpicked(LocalDate d) {
		BillableEntity e = bookingRepository.findFirstByCustomerTypeAndPickingNullAndOrderDateBetween( //
			EX_TRUCK, goLive(), billingCutoff(d));
		return toBookingIdOnlyBillable(e);
	}

	@Override
	protected BillableEntity firstEntity() {
		return bookingRepository.findFirstByCustomerTypeAndRmaNullAndBookingIdGreaterThanOrderByIdAsc(EX_TRUCK, 0L);
	}

	@Override
	protected BillableEntity lastEntity() {
		return bookingRepository.findFirstByCustomerTypeAndRmaNullAndBookingIdGreaterThanOrderByIdDesc(EX_TRUCK, 0L);
	}

	@Override
	protected BillableEntity nextEntity(Long id) {
		return bookingRepository
			.findFirstByCustomerTypeAndRmaNullAndIdGreaterThanAndBookingIdGreaterThanOrderByIdAsc(EX_TRUCK, id, 0L);
	}

	@Override
	protected BillableEntity previousEntity(Long id) {
		return bookingRepository
			.findFirstByCustomerTypeAndRmaNullAndIdLessThanAndBookingIdGreaterThanOrderByIdDesc(EX_TRUCK, id, 0L);
	}
}