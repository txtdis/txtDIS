package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.type.TransactionDirectionType.OUTGOING;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import ph.txtdis.domain.BillableDetailEntity;
import ph.txtdis.domain.BillableEntity;
import ph.txtdis.domain.BomEntity;
import ph.txtdis.domain.ItemEntity;
import ph.txtdis.type.TransactionDirectionType;

public interface QtyPerItemService {

	default List<BomEntity> toBomList(TransactionDirectionType direction, List<BillableEntity> l) {
		return l.stream().flatMap(entity -> entity.getDetails().stream()) //
				.map(detail -> toBom(direction, detail))//
				.collect(groupingBy(BomEntity::getPart, //
						mapping(BomEntity::getQty, reducing(ZERO, BigDecimal::add)))) //
				.entrySet().stream().map(e -> createBom(null, e.getKey(), e.getValue())) //
				.collect(toList());
	}

	default BomEntity toBom(TransactionDirectionType direction, BillableDetailEntity d) {
		BigDecimal qty = d.getReturnedQtyInDecimals();
		if (direction == OUTGOING)
			qty = d.getInitialQtyInDecimals().subtract(d.getSoldQtyInDecimals());
		return createBom(null, d.getItem(), qty);
	}

	default List<BomEntity> toRouteGroupedBomList(List<BillableEntity> l) {
		Map<ItemEntity, List<BomEntity>> map = l.stream() // 
				.flatMap(e -> e.getDetails().stream()) //
				.map(d -> createBom(routeAsAnItem(d), d.getItem(), d.getInitialQty())) //
				.collect(groupingBy(BomEntity::getItem));
		return map.keySet().stream().flatMap(i -> sumQty(map, i).stream()).collect(toList()); //
	}

	default List<BomEntity> sumQty(Map<ItemEntity, List<BomEntity>> map, ItemEntity item) {
		return map.get(item).stream()
				.collect(groupingBy( //
						BomEntity::getPart, mapping(BomEntity::getQty, reducing(BigDecimal.ZERO, BigDecimal::add)))) //
				.entrySet().stream() //
				.map(m -> createBom(item, m.getKey(), m.getValue())) //
				.collect(toList());
	}

	default BomEntity createBom(ItemEntity item, ItemEntity part, BigDecimal qty) {
		BomEntity b = new BomEntity();
		b.setItem(item);
		b.setPart(part);
		b.setQty(qty);
		return b;
	}

	default ItemEntity routeAsAnItem(BillableDetailEntity d) {
		ItemEntity e = new ItemEntity();
		e.setName(d.getBilling().getSuffix());
		return e;
	}
}
