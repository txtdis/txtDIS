package ph.txtdis.mgdc.ccbpi.service.server;

import static java.math.BigDecimal.ZERO;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.QuantityType.ACTUAL;
import static ph.txtdis.type.QuantityType.EXPECTED;
import static ph.txtdis.type.QuantityType.OTHER;
import static ph.txtdis.type.QuantityType.RETURNED;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import ph.txtdis.mgdc.ccbpi.domain.BomEntity;
import ph.txtdis.mgdc.ccbpi.domain.DetailedEntity;
import ph.txtdis.mgdc.ccbpi.domain.ItemEntity;
import ph.txtdis.mgdc.ccbpi.domain.ItemQuantifiedEntityDetail;
import ph.txtdis.type.QuantityType;

@Service("detailsToBomService")
public class DetailsToBomServiceImpl //
	implements DetailsToBomService {

	private static Logger logger = getLogger(DetailsToBomServiceImpl.class);

	@Override
	public List<BomEntity> toEmptiesBomList(QuantityType type, List<? extends DetailedEntity> details) {
		List<BomEntity> contents = toBomList(type, details);
		return toEmptiesBomList(contents);
	}

	@Override
	public List<BomEntity> toBomList(QuantityType type, List<? extends DetailedEntity> entities) {
		return entities == null ? emptyList() //
			: entities.stream() //
			.flatMap(entity -> entity.getDetails().stream()) //
			.map(d -> toBom(d.getItem(), qty(type, d)))
			.collect(groupingBy( //
				BomEntity::getPart, //
				mapping( //
					BomEntity::getQty, //
					reducing(ZERO, BigDecimal::add)))) //
			.entrySet().stream() //
			.map(e -> toBom(e.getKey(), e.getValue())) //
			.collect(Collectors.toList());
	}

	private List<BomEntity> toEmptiesBomList(List<BomEntity> boms) {
		return boms == null ? Collections.emptyList()
			: boms.stream() //
			.map(b -> toBom(item(b).getEmpties(), b.getQty())) //
			.filter(b -> item(b) != null) //
			.collect(Collectors.toList());
	}

	private BomEntity toBom(ItemEntity i, BigDecimal qty) {
		BomEntity b = new BomEntity();
		b.setPart(i);
		b.setQty(qty);
		logger.info("\n    Part&Qty@toBom = " + i + " & " + qty);
		return b;
	}

	private BigDecimal qty(QuantityType type, ItemQuantifiedEntityDetail d) {
		BigDecimal qty = BigDecimal.ZERO;
		if (type == OTHER || type == EXPECTED)
			qty = d.getInitialQty();
		else if (type == ACTUAL)
			qty = d.getFinalQty();
		else if (type == RETURNED)
			qty = d.getReturnedQty();
		else
			throw new RuntimeException("No applicable type");
		logger.info("\n    Qty@qty = " + type + ": " + qty);
		return qty;
	}

	private ItemEntity item(BomEntity b) {
		return b.getPart();
	}
}
