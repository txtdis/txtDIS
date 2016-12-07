package ph.txtdis.service;

import static ph.txtdis.dto.PartnerType.EX_TRUCK;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ph.txtdis.domain.BillableEntity;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.PartnerType;
import ph.txtdis.util.NumberUtils;

@Service("exTruckServiceImpl")
public class ExTruckServiceImpl extends AbstractSpunBillableService implements ExTruckService {

	@Override
	public Billable findByDate(Date d) {
		BillableEntity b = repository.findFirstByBookingIdNotNullAndCustomerTypeAndCreatedOnBetweenOrderByCreatedOnAsc(
				PartnerType.EX_TRUCK, start(d), end(d));
		return toDTO(b);
	}

	@Override
	public List<Billable> findBookedExTrucks(Date d) {
		List<BillableEntity> l = repository.findByOrderDateAndCustomerType(d.toLocalDate(), PartnerType.EX_TRUCK);
		return l == null ? null : l.stream().map(e -> customerOnlyBillable(null, e)).collect(Collectors.toList());
	}

	@Override
	public Billable findLoadOrder(LocalDate date, String exTruck) {
		BillableEntity b = repository.findByCustomerNameAndOrderDate(exTruck, date);
		return toDTO(b);
	}

	@Override
	public Billable findOpenLoadOrder(String seller, Date date) {
		List<BillableEntity> l = repository.findByCustomerTypeAndPickingNotNullAndOrderDateBetween(EX_TRUCK, goLive(),
				billingCutoff(date, seller));
		BillableEntity e = l.stream().flatMap(b -> b.getDetails().stream()) //
				.filter(d -> !NumberUtils.isZero(d.getQtyInDecimals())) //
				.map(d -> d.getBilling()).distinct() //
				.filter(b -> filterBySeller(b, seller)) //
				.findFirst().orElse(null);
		return toBookingIdOnlyBillable(e);
	}

	private boolean filterBySeller(BillableEntity b, String seller) {
		if (seller.equalsIgnoreCase("ALL"))
			return true;
		return seller.equals(b.getCustomer().getSeller());
	}

	@Override
	public Billable next(Long id) {
		BillableEntity b = repository
				.findFirstByCustomerTypeAndBookingIdGreaterThanOrderByBookingIdAsc(PartnerType.EX_TRUCK, id);
		return toDTO(b);
	}

	@Override
	public Billable previous(Long id) {
		BillableEntity b = repository
				.findFirstByCustomerTypeAndBookingIdLessThanOrderByBookingIdDesc(PartnerType.EX_TRUCK, id);
		return toDTO(b);
	}

	@Override
	protected Billable firstSpun() {
		BillableEntity b = repository.findFirstByCustomerTypeAndBookingIdNotNullOrderByBookingIdAsc(PartnerType.EX_TRUCK);
		return toDTO(b);
	}

	@Override
	protected Billable lastSpun() {
		BillableEntity b = repository
				.findFirstByCustomerTypeAndBookingIdNotNullOrderByBookingIdDesc(PartnerType.EX_TRUCK);
		return toDTO(b);
	}
}