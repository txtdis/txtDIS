package ph.txtdis.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.stereotype.Service;

import ph.txtdis.domain.Billing;
import ph.txtdis.domain.Customer;
import ph.txtdis.domain.Picking;
import ph.txtdis.domain.Truck;
import ph.txtdis.domain.User;
import ph.txtdis.dto.Booking;
import ph.txtdis.dto.PickList;

@Service("pickingToPickListService")
public class PickingToPickListService {

	public List<PickList> toPickList(List<Picking> p) {
		return p == null ? null : convert(p);
	}

	public PickList toPickList(Picking p) {
		return p == null ? null : convert(p);
	}

	private List<PickList> convert(List<Picking> l) {
		return l.stream().map(p -> convert(p)).collect(toList());
	}

	private PickList convert(Picking p) {
		PickList l = new PickList();
		l.setId(p.getId());
		l.setTruck(toName(p.getTruck()));
		l.setDriver(toName(p.getDriver()));
		l.setLeadHelper(toName(p.getLeadHelper()));
		l.setAsstHelper(toName(p.getAsstHelper()));
		l.setRemarks(p.getRemarks());
		l.setPrintedBy(p.getPrintedBy());
		l.setPickDate(p.getPickDate());
		l.setBookings(toBookings(p));
		l.setPrintedOn(p.getPrintedOn());
		l.setCreatedBy(p.getCreatedBy());
		l.setCreatedOn(p.getCreatedOn());
		return l;
	}

	private String route(Billing i) {
		try {
			return i.getCustomer().getRouteHistory().stream()
					.filter(r -> r.getStartDate().compareTo(i.getOrderDate()) <= 0)
					.max((a, b) -> a.getStartDate().compareTo(b.getStartDate())).get().getRoute().getName();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Booking toBooking(Billing i) {
		Booking b = new Booking();
		Customer c = i.getCustomer();
		b.setId(i.getBookingId());
		b.setCustomer(c.getName());
		b.setLocation(c.getLocation());
		b.setRoute(route(i));
		return b;
	}

	private List<Booking> toBookings(Picking p) {
		return p.getBillings().stream().map(billing -> toBooking(billing)).collect(toList());
	}

	private String toName(Truck t) {
		return t == null ? null : t.getName();
	}

	private String toName(User u) {
		return u == null ? null : u.getUsername();
	}
}
