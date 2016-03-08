package ph.txtdis.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.Billing;
import ph.txtdis.domain.Picking;
import ph.txtdis.domain.User;
import ph.txtdis.dto.Booking;
import ph.txtdis.dto.PickList;
import ph.txtdis.repository.BillingRepository;
import ph.txtdis.repository.PickListRepository;
import ph.txtdis.repository.TruckRepository;
import ph.txtdis.repository.UserRepository;

@Service("pickListToPickingService")
public class PickListToPickingService {

	@Autowired
	private BillingRepository billingRepo;

	@Autowired
	private PickListRepository pickRepo;

	@Autowired
	private TruckRepository truckRepo;

	@Autowired
	private UserRepository userRepo;

	public Picking toPicking(PickList l) {
		return l == null ? null : convert(l);
	}

	private Picking convert(PickList l) {
		return l.getId() == null ? create(l) : update(l);
	}

	private Picking create(PickList l) {
		Picking p = new Picking();
		p.setTruck(truckRepo.findByName(l.getTruck()));
		p.setDriver(toUser(l.getDriver()));
		p.setLeadHelper(toUser(l.getLeadHelper()));
		p.setAsstHelper(toUser(l.getAsstHelper()));
		p.setRemarks(l.getRemarks());
		p.setPickDate(l.getPickDate());
		p.setBillings(toBillings(l));
		return p;
	}

	private Billing toBilling(Booking k) {
		System.err.println("Found Billing By Booking Id = " + billingRepo.findByBookingId(k.getId()));
		return billingRepo.findByBookingId(k.getId());
	}

	private List<Billing> toBillings(PickList p) {
		return p.getBookings().stream().map(booking -> toBilling(booking)).collect(toList());
	}

	private User toUser(String n) {
		return n == null ? null : userRepo.findOne(n);
	}

	private Picking update(PickList l) {
		Picking p = pickRepo.findOne(l.getId());
		p.setPrintedBy(l.getPrintedBy());
		p.setPrintedOn(l.getPrintedOn());
		return p;
	}
}
