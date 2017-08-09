package ph.txtdis.mgdc.ccbpi.service.server;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.DateTimeUtils.toOrderConfirmationDate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import ph.txtdis.mgdc.ccbpi.domain.BillableDetailEntity;
import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;
import ph.txtdis.mgdc.ccbpi.domain.BomEntity;
import ph.txtdis.mgdc.ccbpi.domain.ItemEntity;
import ph.txtdis.type.TransactionDirectionType;

public interface QtyPerItemService {

	default List<BomEntity> toBomList(TransactionDirectionType direction, List<BillableEntity> l) {
		return toBomMap(direction, l).entrySet().stream().map(e -> createBom(null, e.getKey(), e.getValue())) //
			.collect(toList());
	}

	default Map<ItemEntity, BigDecimal> toBomMap(TransactionDirectionType direction, List<BillableEntity> l) {
		return l.stream().flatMap(entity -> entity.getDetails().stream()) //
			.map(detail -> toBom(direction, detail))//
			.collect(groupingBy( //
				BomEntity::getPart, //
				mapping( //
					BomEntity::getQty, //
					reducing( //
						ZERO, //
						BigDecimal::add)))) //
			;
	}

	default BomEntity createBom(ItemEntity item, ItemEntity part, BigDecimal qty) {
		BomEntity b = new BomEntity();
		b.setItem(item);
		b.setPart(part);
		b.setQty(qty);
		return b;
	}

	default BomEntity toBom(TransactionDirectionType direction, BillableDetailEntity d) {
		BigDecimal qty = d.getReturnedQty();
		return createBom(null, d.getItem(), qty);
	}

	default List<BomEntity> toInitialOrderNoAndCustomerAndRouteGroupedBomList(List<BillableEntity> l) {
		Map<ItemEntity, List<BomEntity>> map = l.stream() // 
			.flatMap(e -> e.getDetails().stream()) //
			.map(d -> createBom(orderNoAndCustomerAndRouteAsAnItem(d), d.getItem(), d.getInitialQty())) //
			.collect(groupingBy(BomEntity::getItem));
		return toBom(map);
	}

	default ItemEntity orderNoAndCustomerAndRouteAsAnItem(BillableDetailEntity d) {
		ItemEntity e = new ItemEntity();
		e.setName(orderNoAndCustomerAndRoute(d.getBilling()));
		return e;
	}

	default List<BomEntity> toBom(Map<ItemEntity, List<BomEntity>> map) {
		return map.keySet().stream().flatMap(i -> sumQty(map, i).stream()).collect(toList());
	}

	default String orderNoAndCustomerAndRoute(BillableEntity b) {
		return b.getCustomer().getId() + "-" + toOrderConfirmationDate(b.getOrderDate()) //
			+ "|" + b.getCustomer().getName() //
			+ "|" + b.getSuffix();
	}

	default List<BomEntity> sumQty(Map<ItemEntity, List<BomEntity>> map, ItemEntity item) {
		return map.get(item).stream()
			.collect(groupingBy( //
				BomEntity::getPart, //
				mapping( //
					BomEntity::getQty, //
					reducing(BigDecimal.ZERO, BigDecimal::add)))) //
			.entrySet().stream() //
			.map(m -> createBom(item, m.getKey(), m.getValue())) //
			.collect(toList());
	}

	default List<BomEntity> toBomList(List<BillableEntity> l) {
		Map<ItemEntity, List<BomEntity>> map = l.stream() // 
			.flatMap(e -> e.getDetails().stream()) //
			.map(d -> createBom(null, d.getItem(), d.getInitialQty())) //
			.collect(groupingBy(BomEntity::getPart));
		return toBom(map); //
	}

	default List<BomEntity> toReturnedOrderNoAndCustomerAndRouteGroupedBomList(List<BillableEntity> l) {
		Map<ItemEntity, List<BomEntity>> map = l.stream() // 
			.flatMap(e -> e.getDetails().stream()) //
			.map(d -> createBom(orderNoAndCustomerAndRouteAsAnItem(d), d.getItem(), d.getReturnedQty())) //
			.collect(groupingBy(BomEntity::getItem));
		return toBom(map); //
	}

	default List<BomEntity> toReturnedBomList(List<BillableEntity> l) {
		Map<ItemEntity, List<BomEntity>> map = l.stream() // 
			.flatMap(e -> e.getDetails().stream()) //
			.map(d -> createBom(null, d.getItem(), d.getReturnedQty())) //
			.collect(groupingBy(BomEntity::getPart));
		return toBom(map); //
	}
}
