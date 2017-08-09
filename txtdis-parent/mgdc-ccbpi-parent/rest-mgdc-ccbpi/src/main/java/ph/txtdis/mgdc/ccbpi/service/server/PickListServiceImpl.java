package ph.txtdis.mgdc.ccbpi.service.server;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.time.ZonedDateTime.now;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.util.DateTimeUtils.toOrderConfirmationDate;
import static ph.txtdis.util.UserUtils.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.TruckEntity;
import ph.txtdis.domain.UserEntity;
import ph.txtdis.dto.Booking;
import ph.txtdis.dto.PickList;
import ph.txtdis.dto.PickListDetail;
import ph.txtdis.exception.FailedPrintingException;
import ph.txtdis.mgdc.ccbpi.domain.BillableDetailEntity;
import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;
import ph.txtdis.mgdc.ccbpi.domain.BomEntity;
import ph.txtdis.mgdc.ccbpi.domain.ChannelEntity;
import ph.txtdis.mgdc.ccbpi.domain.CustomerEntity;
import ph.txtdis.mgdc.ccbpi.domain.ItemEntity;
import ph.txtdis.mgdc.ccbpi.domain.PickListDetailEntity;
import ph.txtdis.mgdc.ccbpi.domain.PickListEntity;
import ph.txtdis.mgdc.ccbpi.printer.PickListPrinter;
import ph.txtdis.mgdc.ccbpi.repository.PickListRepository;
import ph.txtdis.mgdc.domain.ItemFamilyEntity;
import ph.txtdis.mgdc.domain.RouteEntity;
import ph.txtdis.service.AbstractSpunSavedKeyedService;
import ph.txtdis.service.ServerTruckService;
import ph.txtdis.service.ServerUserService;
import ph.txtdis.util.UserUtils;

@Service("pickListService")
public class PickListServiceImpl //
	extends AbstractSpunSavedKeyedService<PickListRepository, PickListEntity, PickList, Long> //
	implements PickListService {

	private static Logger logger = getLogger(PickListServiceImpl.class);

	@Autowired
	private BomService bomService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private OrderConfirmationService orderConfirmationService;

	@Autowired
	private PickListPrinter pickListPrinter;

	@Autowired
	private ServerTruckService truckService;

	@Autowired
	private ServerUserService userService;

	@Override
	public List<PickList> findAllByDate(LocalDate d) {
		List<PickListEntity> l = repository.findByPickDate(d);
		return toModels(l);
	}

	@Override
	public List<PickListEntity> list(String collector, LocalDate start, LocalDate end) {
		return repository
			.findDistinctByPickDateBetweenAndLeadAssistantNameContainingAndBillingsNotNull(start, end, collector);
	}

	@Override
	public PickList printPickList(Long id) throws FailedPrintingException {
		PickListEntity p = findEntityByPrimaryKey(id);
		p = printPickList(p);
		return toModel(p);
	}

	private PickListEntity printPickList(PickListEntity p) throws FailedPrintingException {
		try {
			return printLoadOrder(p);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FailedPrintingException(e.getMessage());
		}
	}

	@Override
	public PickList toModel(PickListEntity e) {
		return e == null ? null : newPickList(e);
	}

	private PickListEntity printLoadOrder(PickListEntity p) throws Exception {
		if (!p.getPickDate().isBefore(LocalDate.now()))
			pickListPrinter.print(p);
		p.setPrintedBy(username());
		p.setPrintedOn(now());
		return repository.save(p);
	}

	private PickList newPickList(PickListEntity e) {
		PickList p = new PickList();
		p.setId(e.getId());
		p.setTruck(toName(e.getTruck()));
		p.setDriver(toName(e.getDriver()));
		p.setLeadAssistant(toName(e.getLeadAssistant()));
		p.setAssistant(toName(e.getAssistant()));
		p.setRemarks(e.getRemarks());
		p.setPrintedBy(e.getPrintedBy());
		p.setPickDate(e.getPickDate());
		p.setBookings(toBookings(e));
		p.setDetails(toDetails(e));
		p.setPrintedOn(e.getPrintedOn());
		p.setCreatedBy(e.getCreatedBy());
		p.setCreatedOn(e.getCreatedOn());
		p.setReceivedBy(e.getReceivedBy());
		p.setReceivedOn(e.getReceivedOn());
		return p;
	}

	private List<Booking> toBookings(PickListEntity p) {
		List<BillableEntity> l = p.getBillings();
		return l == null ? null : toBookings(l);
	}

	private List<PickListDetail> toDetails(PickListEntity e) {
		List<PickListDetailEntity> l = e.getDetails();
		return l == null ? null : l.stream().map(d -> toDetail(d)).collect(Collectors.toList());
	}

	private List<Booking> toBookings(List<BillableEntity> s) {
		return s.stream().map(b -> toBooking(b)).collect(Collectors.toList());
	}

	private PickListDetail toDetail(PickListDetailEntity e) {
		PickListDetail d = new PickListDetail();
		ItemEntity i = e.getItem();
		d.setId(i.getId());
		d.setItemName(i.getName());
		d.setItemVendorNo(i.getVendorId());
		d.setQtyPerCase(itemService.getCountPerCase(i));
		d.setPickedQty(e.getInitialQty());
		d.setReturnedQty(e.getReturnedQty());
		return d;
	}

	private Booking toBooking(BillableEntity e) {
		Booking b = new Booking();
		CustomerEntity c = e.getCustomer();
		b.setId(e.getId());
		b.setBookingId(e.getBookingId());
		b.setCustomer(c.getName());
		b.setDeliveryRoute(routeName(c));
		b.setLocation(ocsNo(e));
		b.setRoute(channelName(c));
		logger.info("\n    Booking@toBooking = " + b);
		return b;
	}

	private String routeName(CustomerEntity c) {
		RouteEntity r = c.getRoute();
		return r == null ? null : r.getName();
	}

	private String ocsNo(BillableEntity e) {
		return e.getCustomer().getVendorId() //
			+ "-" + toOrderConfirmationDate(e.getOrderDate()) //
			+ "/" + e.getBookingId();
	}

	private String channelName(CustomerEntity c) {
		ChannelEntity ch = c.getChannel();
		return ch == null ? null : ch.getName();
	}

	@Override
	public PickList save(PickList p) {
		PickListEntity b = post(p);
		return toModel(b);
	}

	private PickListEntity post(PickList p) {
		PickListEntity e = toEntity(p);
		e.setDetails(toDetails(e, summaryOfQuantitiesPerItem(e)));
		return post(e);
	}

	@Override
	public List<BomEntity> summaryOfQuantitiesPerItem(PickListEntity p) {
		List<BomEntity> l = p.getBillings().stream() //
			.flatMap(b -> b.getDetails().stream())//
			.flatMap(d -> toBoms(d).stream())//
			.collect(groupingBy( //
				BomEntity::getPart, //
				mapping( //
					BomEntity::getQty, //
					reducing( //
						ZERO, //
						BigDecimal::add)))) //
			.entrySet().stream()//
			.map(e -> toBom(e)) //
			.sorted((a, b) -> familyId(a).compareTo(familyId(b))) //
			.collect(Collectors.toList());
		return l == null ? null : addEmpties(l);
	}

	private List<BomEntity> toBoms(BillableDetailEntity d) {
		List<BomEntity> l = bomService.extractAll(d.getItem(), ONE);
		return toQtyMultipliedBoms(l, d.getInitialQty());
	}

	private List<BomEntity> toQtyMultipliedBoms(List<BomEntity> boms, BigDecimal qty) {
		return boms.stream().map(b -> multiplyQtyPerBom(b, qty)).collect(toList());
	}

	private BomEntity multiplyQtyPerBom(BomEntity b, BigDecimal qty) {
		return bomService.createComponentOnly(b.getPart(), b.getQty().multiply(qty));
	}

	private BomEntity toBom(Entry<ItemEntity, BigDecimal> e) {
		return bomService.createComponentOnly(e.getKey(), e.getValue());
	}

	private Long familyId(BomEntity a) {
		ItemFamilyEntity family = a.getPart().getFamily();
		return family == null ? 0L : family.getId();
	}

	private List<BomEntity> addEmpties(List<BomEntity> boms) {
		logger.info("\n    Items@addEmpties = " + boms);
		List<BomEntity> empties = empties(boms);
		boms.addAll(empties);
		logger.info("\n    Empties@addEmpties = " + empties);
		List<BomEntity> bottles = bottles(empties);
		boms.addAll(bottles);
		logger.info("\n    Bottles@addEmpties = " + bottles);
		List<BomEntity> shells = shells(bottles);
		boms.addAll(shells);
		logger.info("\n    Shells@addEmpties = " + shells);
		return boms;
	}

	private List<BomEntity> bottles(List<BomEntity> boms) {
		return empties(boms);
	}

	private List<BomEntity> shells(List<BomEntity> boms) {
		return empties(boms);
	}

	private List<BomEntity> empties(List<BomEntity> boms) {
		return boms.stream() //
			.map(b -> b.getPart().getEmpties()) //
			.filter(i -> i != null).distinct() //
			.map(i -> toEmptiesBom(i)) //
			.collect(Collectors.toList());
	}

	private BomEntity toEmptiesBom(ItemEntity i) {
		BomEntity b = new BomEntity();
		b.setPart(i);
		b.setQty(ZERO);
		return b;
	}

	private String orderConfirmationNo(Booking b) {
		return b.getLocation();
	}

	private List<PickListDetailEntity> toDetails(PickListEntity p, List<BomEntity> boms) {
		return boms.stream().map(b -> toDetail(p, b)).collect(Collectors.toList());
	}

	private PickListDetailEntity toDetail(PickListEntity p, BomEntity b) {
		PickListDetailEntity d = new PickListDetailEntity();
		d.setPicking(p);
		d.setItem(b.getPart());
		d.setPickedCount(b.getQty().intValue());
		return d;
	}

	@Override
	public PickListEntity toEntity(PickList l) {
		return l.getId() == null ? create(l) : update(l);
	}

	private PickListEntity create(PickList p) {
		PickListEntity e = new PickListEntity();
		e.setTruck(truckService.findEntityByName(p.getTruck()));
		e.setDriver(toUser(p.getDriver()));
		e.setLeadAssistant(toUser(p.getLeadAssistant()));
		e.setAssistant(toUser(p.getAssistant()));
		e.setRemarks(p.getRemarks());
		e.setPickDate(p.getPickDate());
		e.setBillings(toBillings(e, p));
		e.setPrintedBy(getPrintedBy(p));
		e.setPrintedOn(getPrintedOn(p));
		return e;
	}

	private UserEntity toUser(String n) {
		return n == null ? null : userService.findEntityByPrimaryKey(n);
	}

	private List<BillableEntity> toBillings(PickListEntity e, PickList p) {
		List<Booking> l = p.getBookings();
		return l == null ? null : toBillings(e, l);
	}

	private List<BillableEntity> toBillings(PickListEntity e, List<Booking> l) {
		return l.stream().map(b -> toBilling(b, e)).collect(Collectors.toList());
	}

	private BillableEntity toBilling(Booking b, PickListEntity p) {
		BillableEntity e = orderConfirmationService.findEntityByBookingNo(orderConfirmationNo(b));
		e.setPicking(p);
		return e;
	}

	private String getPrintedBy(PickList p) {
		String user = p.getPrintedBy();
		return user == null ? null : user;
	}

	private ZonedDateTime getPrintedOn(PickList p) {
		ZonedDateTime zdt = p.getPrintedOn();
		return zdt == null ? null : zdt;
	}

	private PickListEntity update(PickList p) {
		PickListEntity e = findEntityByPrimaryKey(p.getId());
		e.setBillings(toBillings(e, p));
		e.setDetails(toDetails(e, p));
		e.setPrintedBy(p.getPrintedBy());
		e.setPrintedOn(p.getPrintedOn());
		return e;
	}

	private List<PickListDetailEntity> toDetails(PickListEntity e, PickList p) {
		try {
			Map<Long, PickListDetailEntity> map = new HashMap<>();
			for (PickListDetailEntity d : e.getDetails())
				map.put(d.getItem().getId(), d);
			for (PickListDetail d : p.getDetails()) {
				PickListDetailEntity de = map.get(d.getId());
				if (de != null) {
					de.setReturnedCount(d.getReturnedQty().intValue());
					map.put(d.getId(), de);
				}
			}
			return new ArrayList<>(map.values());
		} catch (Exception x) {
			return null;
		}
	}

	private String toName(TruckEntity t) {
		return t == null ? null : t.getName();
	}

	private String toName(UserEntity u) {
		return u == null ? null : u.getName();
	}
}