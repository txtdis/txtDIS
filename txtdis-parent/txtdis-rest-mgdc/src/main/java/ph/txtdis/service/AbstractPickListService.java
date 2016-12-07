package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static java.time.ZonedDateTime.now;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;
import static org.apache.log4j.Logger.getLogger;

import java.math.BigDecimal;
import java.sql.Date;
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

import ph.txtdis.domain.BillableDetailEntity;
import ph.txtdis.domain.BillableEntity;
import ph.txtdis.domain.BomEntity;
import ph.txtdis.domain.CustomerEntity;
import ph.txtdis.domain.ItemEntity;
import ph.txtdis.domain.ItemFamilyEntity;
import ph.txtdis.domain.PickListDetailEntity;
import ph.txtdis.domain.PickListEntity;
import ph.txtdis.domain.RouteEntity;
import ph.txtdis.domain.TruckEntity;
import ph.txtdis.domain.UserEntity;
import ph.txtdis.dto.Booking;
import ph.txtdis.dto.PickList;
import ph.txtdis.dto.PickListDetail;
import ph.txtdis.exception.FailedPrintingException;
import ph.txtdis.printer.PickListPrinter;
import ph.txtdis.repository.PickListRepository;

public abstract class AbstractPickListService
		extends AbstractSpunService<PickListRepository, PickListEntity, PickList, Long> implements PickListService {

	private static Logger logger = getLogger(AbstractPickListService.class);

	@Autowired
	private BomService bomService;

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private PrimaryTruckService truckService;

	@Autowired
	private UserService userService;

	@Autowired
	private PickListPrinter pickListPrinter;

	@Autowired
	protected BillableService billableService;

	@Override
	public PickList printPickList(Long id) throws FailedPrintingException {
		PickListEntity p = findEntity(id);
		p = printPickList(p);
		return toDTO(p);
	}

	private PickListEntity printPickList(PickListEntity p) throws FailedPrintingException {
		try {
			logger.info("\n    PicklistToPrint: " + p.getPickDate() + ", " + p.getTruck() + "[\n    " + p.getBillings());
			return printLoadOrder(p);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FailedPrintingException(e.getMessage());
		}
	}

	private PickListEntity printLoadOrder(PickListEntity p) throws Exception {
		if (!p.getPickDate().isBefore(LocalDate.now()))
			pickListPrinter.print(p);
		p.setPrintedBy(credentialService.username());
		p.setPrintedOn(now());
		return repository.save(p);
	}

	@Override
	public PickList findById(Long id) {
		return toDTO(findEntity(id));
	}

	@Override
	public List<PickList> findByDate(Date d) {
		List<PickListEntity> l = repository.findByPickDate(d.toLocalDate());
		return toList(l);
	}

	@Override
	public PickListEntity toEntity(PickList l) {
		return l.getId() == null ? create(l) : update(l);
	}

	private PickListEntity create(PickList p) {
		PickListEntity e = new PickListEntity();
		e.setTruck(truckService.findEntityByName(p.getTruck()));
		e.setDriver(toUser(p.getDriver()));
		e.setLeadHelper(toUser(p.getHelper()));
		e.setAsstHelper(toUser(p.getThirdPerson()));
		e.setRemarks(p.getRemarks());
		e.setPickDate(p.getPickDate());
		e.setBillings(getBillings(e, p));
		e.setPrintedBy(getPrintedBy(p));
		e.setPrintedOn(getPrintedOn(p));
		logger.info("\n    PickListEntity: " + e.getPickDate() + " - " + e.getTruck() + " - " + e.getBillings());
		return e;
	}

	private UserEntity toUser(String n) {
		return n == null ? null : userService.findEntity(n);
	}

	private List<BillableEntity> getBillings(PickListEntity e, PickList p) {
		List<Booking> l = p.getBookings();
		return l == null ? null : l.stream().map(b -> toBilling(e, b)).collect(Collectors.toList());
	}

	protected BillableEntity toBilling(PickListEntity p, Booking k) {
		BillableEntity e = billableService.findEntityByLoadOrSalesOrderId(k.getId());
		e.setPicking(p);
		return e;
	}

	@Override
	public List<BomEntity> summaryOfQuantitiesPerItem(PickListEntity p) {
		List<BillableDetailEntity> l = billableService.getDetails(p);
		logger.info("\n    BillableDetailEntityListToSummarize: " + l);
		return l == null ? null
				: l.stream()//
						.map(d -> toBoms(d)).flatMap(List::stream)//
						.collect(groupingBy(BomEntity::getPart, //
								mapping(BomEntity::getQty, reducing(ZERO, BigDecimal::add)))) //
						.entrySet().stream().map(e -> toBom(e)) //
						.sorted((a, b) -> familyId(a).compareTo(familyId(b))) //
						.collect(Collectors.toList());
	}

	private List<BomEntity> toBoms(BillableDetailEntity d) {
		List<BomEntity> l = bomService.listBomEntities(d);
		logger.info("\n    BomEntityListFromBillableDetailEntity: " + l);
		return l == null || l.isEmpty() ? asList(toBom(d)) : toQuantifiedBoms(l, d.getInitialUnitQty());
	}

	protected abstract BomEntity toBom(BillableDetailEntity d);

	private BomEntity toBom(Entry<ItemEntity, BigDecimal> e) {
		logger.info("\n    BomEntityFromEntry: " + e.getKey() + ", " + e.getValue());
		return createBom(e.getKey(), e.getValue());
	}

	protected BomEntity createBom(ItemEntity i, BigDecimal qty) {
		BomEntity b = new BomEntity();
		b.setPart(i);
		b.setQty(qty);
		logger.info("\n    BomEntityCreated: " + b);
		return b;
	}

	private List<BomEntity> toQuantifiedBoms(List<BomEntity> boms, BigDecimal qty) {
		boms.forEach(b -> b.setQty(b.getQty().multiply(qty)));
		return boms;
	}

	private Long familyId(BomEntity a) {
		ItemFamilyEntity family = a.getPart().getFamily();
		return family == null ? 0L : family.getId();
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
		PickListEntity e = findEntity(p.getId());
		e.setDetails(toDetails(e, p));
		e.setPrintedBy(p.getPrintedBy());
		e.setPrintedOn(p.getPrintedOn());
		return e;
	}

	private List<PickListDetailEntity> toDetails(PickListEntity e, PickList p) {
		List<PickListDetail> details = p.getDetails();
		List<PickListDetailEntity> entities = e.getDetails();
		if (details == null || entities == null)
			return null;
		Map<Long, PickListDetailEntity> map = pickListDetailEntityWithReturnedQtyMap(entities, details);
		return new ArrayList<>(map.values());
	}

	private Map<Long, PickListDetailEntity> pickListDetailEntityWithReturnedQtyMap(List<PickListDetailEntity> entities,
			List<PickListDetail> details) {
		Map<Long, PickListDetailEntity> map = pickListDetailEntityMap(entities);
		for (PickListDetail detail : details)
			map = putReturnedQty(map, detail.getId(), detail.getReturnedQty());
		return map;
	}

	private Map<Long, PickListDetailEntity> putReturnedQty(Map<Long, PickListDetailEntity> m, Long id,
			BigDecimal returnedQty) {
		PickListDetailEntity detailEntity = m.get(id);
		if (detailEntity != null && returnedQty != null) {
			detailEntity.setReturnedQty(returnedQty.intValue());
			m.put(id, detailEntity);
		}
		return m;
	}

	private Map<Long, PickListDetailEntity> pickListDetailEntityMap(List<PickListDetailEntity> details) {
		Map<Long, PickListDetailEntity> map = new HashMap<>();
		for (PickListDetailEntity d : details)
			map.put(d.getItem().getId(), d);
		return map;
	}

	@Override
	public PickList save(PickList p) {
		PickListEntity b = post(p);
		return toDTO(b);
	}

	protected PickListEntity post(PickList p) {
		logger.info("\n    PickListToPost: " + p.getPickDate() + " - " + p.getTruck() + " - " + p.getBookings());
		PickListEntity e = repository.save(toEntity(p));
		return e;
	}

	@Override
	public PickList toDTO(PickListEntity e) {
		return e == null ? null : newPickList(e);
	}

	private PickList newPickList(PickListEntity e) {
		PickList p = new PickList();
		p.setId(e.getId());
		p.setTruck(toName(e.getTruck()));
		p.setDriver(toName(e.getDriver()));
		p.setHelper(toName(e.getLeadHelper()));
		p.setThirdPerson(toName(e.getAsstHelper()));
		p.setRemarks(e.getRemarks());
		p.setPrintedBy(e.getPrintedBy());
		p.setPickDate(e.getPickDate());
		p.setBookings(toBookings(e));
		p.setDetails(toDetails(e));
		p.setPrintedOn(e.getPrintedOn());
		p.setCreatedBy(e.getCreatedBy());
		p.setCreatedOn(e.getCreatedOn());
		logger.info("\n    PickList: " + p.getPickDate() + " - " + p.getTruck());
		return p;
	}

	private String toName(TruckEntity t) {
		return t == null ? null : t.getName();
	}

	private String toName(UserEntity u) {
		return u == null ? null : u.getUsername();
	}

	private List<Booking> toBookings(PickListEntity p) {
		List<BillableEntity> l = p.getBillings();
		return l == null ? null : l.stream().map(b -> toBooking(b)).collect(Collectors.toList());
	}

	protected Booking toBooking(BillableEntity e) {
		Booking b = new Booking();
		CustomerEntity c = e.getCustomer();
		b.setId(e.getId());
		b.setBookingId(e.getBookingId());
		b.setCustomer(c.getName());
		b.setLocation(c.getLocation());
		b.setRoute(getRoute(c));
		return b;
	}

	private String getRoute(CustomerEntity c) {
		RouteEntity r = c.getRoute();
		return r == null ? null : r.getName();
	}

	private List<PickListDetail> toDetails(PickListEntity e) {
		List<PickListDetailEntity> l = e.getDetails();
		return l == null ? null : l.stream().map(d -> toDetail(d)).collect(Collectors.toList());
	}

	private PickListDetail toDetail(PickListDetailEntity e) {
		PickListDetail d = new PickListDetail();
		ItemEntity i = e.getItem();
		d.setId(i.getId());
		d.setItemName(i.getName());
		d.setItemVendorNo(i.getVendorId());
		d.setQtyPerCase(itemService.getQtyPerCase(i));
		d.setPickedQty(new BigDecimal(e.getPickedQty()));
		d.setReturnedQty(new BigDecimal(e.getReturnedQty()));
		return d;
	}
}