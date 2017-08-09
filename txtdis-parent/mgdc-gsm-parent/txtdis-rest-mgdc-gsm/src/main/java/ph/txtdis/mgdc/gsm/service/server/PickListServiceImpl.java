package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.Booking;
import ph.txtdis.dto.PickList;
import ph.txtdis.mgdc.gsm.domain.*;
import ph.txtdis.service.RestClientService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

@Service("pickListService")
public class PickListServiceImpl //
	extends AbstractPickListService<LoadOrderService> //
	implements PickListService {

	@Autowired
	private SalesOrderService bookingService;

	@Autowired
	private RestClientService<PickList> restClientService;

	@Override
	public List<PickList> findAll() {
		List<BillableEntity> l = bookingService.findAllPicked();
		return toModels(toPickings(l, PickListDetailEntity::getInitialQty));
	}

	private List<PickListEntity> toPickings(List<BillableEntity> l,
	                                        Function<PickListDetailEntity, BigDecimal> qtyToSum) {
		return l.stream() //
			.flatMap(b -> b.getDetails().stream()) //
			.flatMap(d -> toPickingDetails(d).stream()) //
			.collect(groupingBy( //
				PickListDetailEntity::getPicking, //
				groupingBy( //
					PickListDetailEntity::getItem, //
					mapping( //
						qtyToSum, //
						reducing( //
							ZERO, //
							BigDecimal::add))))) //
			.entrySet().stream() //
			.map(s -> toPicklist(s)) //
			.sorted(comparing(PickListEntity::getId)) //
			.collect(toList());
	}

	private List<PickListDetailEntity> toPickingDetails(BillableDetailEntity d) {
		List<BomEntity> boms = bomService.extractAll(d.getItem(), ONE);
		return boms.stream() //
			.map(bom -> toPickingDetail(d, bom)) //
			.collect(toList());
	}

	private PickListEntity toPicklist(Entry<PickListEntity, Map<ItemEntity, BigDecimal>> s) {
		PickListEntity p = s.getKey();
		p.setDetails(toPicklistDetails(p, s));
		return p;
	}

	private PickListDetailEntity toPickingDetail(BillableDetailEntity b, BomEntity bom) {
		PickListDetailEntity d = new PickListDetailEntity();
		d.setPicking(b.getBilling().getPicking());
		d.setItem(bom.getPart());
		d.setPickedCount(pickedCount(b, bom));
		d.setReturnedCount(returnedCount(b, bom));
		return d;
	}

	private List<PickListDetailEntity> toPicklistDetails(PickListEntity p,
	                                                     Entry<PickListEntity, Map<ItemEntity, BigDecimal>> s) {
		return s.getValue() //
			.entrySet().stream() //
			.map(m -> toPicklistDetail(p, m)) //
			.filter(d -> d.getPickedCount() > 0) //
			.collect(toList());
	}

	private int pickedCount(BillableDetailEntity b, BomEntity bom) {
		BigDecimal qty = b.getInitialQty().multiply(bom.getQty());
		return qty.intValue();
	}

	private int returnedCount(BillableDetailEntity b, BomEntity bom) {
		BigDecimal qty = b.getReturnedQty().multiply(bom.getQty());
		return qty.intValue();
	}

	private PickListDetailEntity toPicklistDetail(PickListEntity p, Entry<ItemEntity, BigDecimal> m) {
		PickListDetailEntity d = new PickListDetailEntity();
		d.setPicking(p);
		d.setItem(m.getKey());
		d.setPickedCount(m.getValue().intValue());
		return d;
	}

	@Override
	public List<PickList> findAllWithReturns() {
		List<BillableEntity> l = bookingService.findAllPickedWithReturns();
		return toModels(toPickings(l, PickListDetailEntity::getReturnedQty));
	}

	@Override
	protected PickListEntity post(PickList p) {
		PickListEntity e = super.post(p);
		List<PickListEntity> l = toPickings(e.getBillings(), PickListDetailEntity::getInitialQty);
		return l.get(0);
	}

	@Override
	public void postToEdms(BillableEntity b) {
		try {
			List<PickListEntity> l = toPickings(asList(b), PickListDetailEntity::getReturnedQty);
			saveToEdms(toModel(l.get(0)));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public PickList saveToEdms(PickList p) throws Exception {
		restClientService.module("pickList").save(p);
		return p;
	}

	@Override
	public PickList save(PickList p) {
		try {
			p = super.save(p);
			return saveToEdms(p);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected List<Booking> toBookings(List<BillableEntity> s) {
		Predicate<BillableEntity> anExTruck = b -> b.getCustomer().getName().startsWith("EX-TRUCK");
		if (s.stream().anyMatch(anExTruck))
			s = s.stream().filter(anExTruck).collect(Collectors.toList());
		return super.toBookings(s);
	}
}