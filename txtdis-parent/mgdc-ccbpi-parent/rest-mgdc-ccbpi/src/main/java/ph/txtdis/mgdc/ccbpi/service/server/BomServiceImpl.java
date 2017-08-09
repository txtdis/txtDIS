package ph.txtdis.mgdc.ccbpi.service.server;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Bom;
import ph.txtdis.mgdc.ccbpi.domain.BomEntity;
import ph.txtdis.mgdc.ccbpi.domain.ItemEntity;
import ph.txtdis.mgdc.ccbpi.dto.Item;

@Service("bomService")
public class BomServiceImpl //
	implements BomService {

	@Autowired
	private ReceivingService receivingService;

	@Autowired
	private ItemService itemService;

	@Override
	public List<Bom> extractAll(Long itemId, String itemName, BigDecimal qty) {
		try {
			Item i = itemService.findByPrimaryKey(itemId);
			List<Bom> boms = new ArrayList<>();
			if (i.getBoms() == null || i.getBoms().isEmpty())
				boms.add(createComponentOnly(itemId, itemName, qty));
			else
				for (Bom bom : i.getBoms())
					boms.addAll(extractAll(bom.getId(), bom.getPart(), qtyInCases(bom)));
			return boms;
		} catch (Exception e) {
			return asList(createComponentOnly(itemId, itemName, qty));
		}
	}

	@Override
	public List<BomEntity> extractAll(ItemEntity i, BigDecimal qty) {
		try {
			List<BomEntity> boms = new ArrayList<>();
			if (i.getBoms().isEmpty())
				boms.add(createComponentOnly(i, qty));
			else
				for (BomEntity bom : i.getBoms())
					boms.addAll(extractAll(bom.getPart(), bom.getQty()));
			return boms;
		} catch (Exception e) {
			e.printStackTrace();
			return asList(createComponentOnly(i, qty));
		}
	}

	@Override
	public List<Bom> getBadIncomingList(LocalDate start, LocalDate end) {
		List<BomEntity> l = receivingService.listBadItemsIncomingQty(start, end);
		return toBoms(l);
	}

	@Override
	public List<Bom> toBoms(List<BomEntity> l) {
		return l == null ? null : l.stream().map(e -> toBom(e)).collect(toList());
	}

	private Bom toBom(BomEntity e) {
		ItemEntity i = e.getPart();
		Bom b = createComponentOnly(i.getId(), i.getName(), e.getQty());
		b.setCreatedBy(e.getCreatedBy());
		b.setCreatedOn(e.getCreatedOn());
		return b;
	}

	private Bom createComponentOnly(Long itemId, String itemName, BigDecimal qty) {
		Bom b = new Bom();
		b.setId(itemId);
		b.setPart(itemName);
		b.setQty(qty);
		return b;
	}

	@Override
	public List<Bom> getBadOutgoingList(LocalDate start, LocalDate end) {
		List<BomEntity> l = receivingService.listBadItemsOutgoingQty(start, end);
		return toBoms(l);
	}

	@Override
	public Bom getExpandedQtyInCases(Long id, String name, BigDecimal qty) {
		List<Bom> l = extractAll(id, name, qty);
		qty = l.stream().map(b -> qtyInCases(b)).reduce(ZERO, BigDecimal::add);
		return createComponentOnly(id, name, qty);
	}

	private BigDecimal qtyInCases(Bom b) {
		return itemService.getQtyPerCase(b.getPart());
	}

	@Override
	public List<Bom> getGoodIncomingList(LocalDate start, LocalDate end) {
		List<BomEntity> l = receivingService.listGoodItemsIncomingQty(start, end);
		return toBoms(l);
	}

	@Override
	public List<Bom> getGoodOutgoingList(LocalDate start, LocalDate end) {
		List<BomEntity> l = receivingService.listGoodItemsOutgoingQty(start, end);
		return toBoms(l);
	}

	@Override
	public List<BomEntity> toEntities(ItemEntity e, Item i) {
		List<Bom> l = i.getBoms();
		return l == null ? null : l.stream().map(b -> toEntity(e, b)).collect(toList());
	}

	private BomEntity toEntity(ItemEntity i, Bom b) {
		ItemEntity part = itemService.findEntityByName(b.getPart());
		BomEntity e = createComponentOnly(part, b.getQty());
		e.setItem(i);
		return e;
	}

	@Override
	public BomEntity createComponentOnly(ItemEntity part, BigDecimal qty) {
		BomEntity e = new BomEntity();
		e.setPart(part);
		e.setQty(qty);
		return e;
	}
}