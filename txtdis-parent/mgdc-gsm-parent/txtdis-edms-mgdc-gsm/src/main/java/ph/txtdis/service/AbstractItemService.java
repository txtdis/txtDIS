package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.domain.EdmsInvoiceDetail;
import ph.txtdis.domain.EdmsItem;
import ph.txtdis.domain.EdmsPurchaseReceiptDetail;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Item;
import ph.txtdis.dto.ItemVendorNo;
import ph.txtdis.dto.PickListDetail;
import ph.txtdis.dto.Price;
import ph.txtdis.dto.QtyPerUom;
import ph.txtdis.repository.EdmsItemRepository;
import ph.txtdis.type.ItemType;
import ph.txtdis.type.UomType;
import ph.txtdis.util.Code;
import ph.txtdis.util.DateTimeUtils;

public abstract class AbstractItemService implements ItemService, DealerPriced {

	private static Logger logger = getLogger(AbstractItemService.class);

	private static final LocalDate GO_LIVE_DATE = LocalDate.of(2016, 3, 15);

	private static final ZonedDateTime GO_LIVE_TIMESTAMP = GO_LIVE_DATE.atStartOfDay(ZoneId.systemDefault());

	@Autowired
	private EdmsItemRepository edmsItemRepository;

	@Autowired
	private EdmsItemFamilyService familyService;

	@Override
	public EdmsItem getItem(ItemVendorNo d) {
		return edmsItemRepository.findByCode(d.getItemVendorNo());
	}

	@Override
	public Item save(Item i) {
		EdmsItem e = edmsItemRepository.findByCode(i.getVendorId());
		edmsItemRepository.save(e == null ? createItem(i) : updateItem(e, i));
		return i;
	}

	protected abstract EdmsItem createItem(Item i);

	protected EdmsItem toEdmsItem(Item i) {
		EdmsItem e = new EdmsItem();
		e.setCode(i.getVendorId());
		e.setName(i.getName());
		e.setPackaging(getPackaging(i));
		e.setClazz(Code.LIQUOR);
		e.setCategory(i.getFamily().getName());
		e.setBrand(null);
		e.setToMilliliterQty(BigDecimal.ZERO);
		e.setConversionFactorQty(BigDecimal.ZERO);
		e.setKept(true);
		e.setSold(true);
		e.setBought(true);
		e.setLotNumbered(false);
		e.setReceiptDated(false);
		e.setExpiryDated(false);
		e.setProductionDated(false);
		e.setPurchaseQty(BigDecimal.ZERO);
		e.setMinQty(BigDecimal.ZERO);
		e.setMaxQty(BigDecimal.ZERO);
		e.setCreatedBy(i.getCreatedBy());
		e.setCreatedOn(DateTimeUtils.toTimestampText(i.getCreatedOn()));
		e = updateItem(e, i);
		return e;
	}

	private boolean isActive(Item i) {
		return i.getDeactivatedOn() == null;
	}

	private int getVolume(Item item) {
		return getQtyPer(item, UomType.L).multiply(new BigDecimal("1000")).intValue();
	}

	private String getPackaging(Item i) {
		return getVolume(i) + "mL x " + getBottlesPerCase(i) + " x " + getCasesPerPallet(i) + "P";
	}

	protected BigDecimal getBottlesPerCase(Item item) {
		return getQtyPer(item, UomType.CS);
	}

	private BigDecimal getCasesPerPallet(Item item) {
		return getQtyPer(item, UomType.PLT);
	}

	private BigDecimal getQtyPer(Item item, UomType uom) {
		return item.getQtyPerUomList().stream()//
				.filter(q -> q.getUom() == uom)//
				.findAny().get().getQty();
	}

	protected BigDecimal getPricePerBottle(Item i) {
		try {
			return i.getPriceList().stream()//
					.filter(p -> p.getType().equals(dealerPrice()) //
							&& p.getIsValid() != null && p.getIsValid() //
							&& p.getStartDate().compareTo(LocalDate.now()) <= 0)
					.max(Price::compareTo)//
					.get().getPriceValue();
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}

	private EdmsItem updateItem(EdmsItem e, Item i) {
		updatePrices(i);
		e.setActive(isActive(i));
		e.setModifiedBy(i.getLastModifiedBy());
		e.setModifiedOn(DateTimeUtils.to24HourTimestampText(i.getLastModifiedOn()));
		return e;
	}

	private void updatePrices(Item i) {
		updatePricePerBottle(i);
		updatePricePerCase(i);
	}

	protected abstract BigDecimal updatePricePerBottle(Item i);

	protected abstract void updatePricePerCase(Item i);

	@Override
	public BigDecimal getPricePerCase(BillableDetail d) {
		return getPricePerCase(d.getItemVendorNo());
	}

	@Override
	public BigDecimal getPricePerCase(EdmsInvoiceDetail i) {
		return getPricePerCase(i.getItemCode());
	}

	protected abstract BigDecimal getPricePerCase(String itemCode);

	@Override
	public List<String> listClassNames() {
		return listByFamily(//
				e -> e.getClazz().toUpperCase().trim(), //
				e -> true);
	}

	private List<String> listByFamily(Function<EdmsItem, String> toFamily, Predicate<EdmsItem> family) {
		Iterable<EdmsItem> i = edmsItemRepository.findAll();
		return StreamSupport.stream(i.spliterator(), false).filter(family).map(toFamily).distinct()
				.collect(Collectors.toList());
	}

	@Override
	public List<String> listCategoryNames(String clazz) {
		return listByFamily(//
				e -> e.getCategory().toUpperCase().trim(), //
				e -> e.getClazz().trim().equalsIgnoreCase(clazz));
	}

	@Override
	public List<String> listBrandNames(String category) {
		return listByFamily(//
				e -> e.getBrand().toUpperCase().trim(), //
				e -> e.getCategory().trim().equalsIgnoreCase(category));
	}

	@Override
	public Item findById(Long id) {
		EdmsItem e = edmsItemRepository.findByCode(id.toString());
		return e == null ? null : toDTO(e);
	}

	private Item toDTO(EdmsItem e) {
		Item i = new Item();
		i.setName(fitName(e.getName()));
		i.setDescription(fitDescription(e));
		i.setFamily(familyService.toBrand(e.getBrand()));
		i.setPriceList(toPriceList(e));
		i.setQtyPerUomList(toQtyPerUomList(e));
		i.setType(ItemType.PURCHASED);
		i.setVendorId(e.getCode());
		logger.info("\n    Item: " + i);
		return i;
	}

	private String fitName(String name) {
		return name.replace(" ", "")//
				.replace("Gin", " GIN")//
				.replace("GSM", "")//
				.replace("ml", "")//
				.replace("mL", "")//
				.replace("x24", "")//
				.replace("Brandy", "")//
				.replace("Gran", "")//
				.replace("Primo", "")//
				.replace("Classic", "")//
				.replace("Package", "")//
				.replace("Vodka", "")//
				.replace("Blue", "BLU")//
				.replace("Vino", "V'")//
				.replace("PremiumGin", "PREMIUM")//
				.replace("Light", " LTE")//
				.replace("-Apple", "APLE")//
				.replace("-Lychee", "LYCH")//
				.replace("-Mojito", "MJTO")//
				.replace("-B.Coffee", "COFE")//
				.replace("-Melon", "MLON")//
				.replace("-Currant", "CUNT")//
				.replace("-Espresso", "ESSO")//
				.replace("-Dalandan", "DLDN")//
				.replace("Angelito", "A'LTO")//
				.replace("Frasquito", "F'QTO")//
				.replace("DonEnriqueMixkila", "D'QUE")//
				.replace("DistilledSpirit", " DS")//
				.replace("RoundFiesta", "FIESTA")//
				.replace("Matador", "M'DOR")//
				.replace("Antonov", "A'NOV")//
				.toUpperCase();
	}

	private String fitDescription(EdmsItem e) {
		return e.getName().replace(" x 24", "")//
				.toUpperCase()//
				+ " x " + getBottlesPerCaseText(e)//
				+ " x " + getCasesPerPalletText(e) + "P";
	}

	private String getBottlesPerCaseText(EdmsItem e) {
		String s = e.getPackaging();
		s = StringUtils.substringAfter(s, "x ");
		return StringUtils.substringBefore(s, " ");
	}

	private String getCasesPerPalletText(EdmsItem e) {
		String s = e.getPackaging();
		s = StringUtils.substringAfterLast(s, " ");
		return StringUtils.substringBefore(s, "P");
	}

	private List<Price> toPriceList(EdmsItem e) {
		Price p = new Price();
		p.setPriceValue(getPricePerCase(e));
		p.setStartDate(GO_LIVE_DATE);
		p.setIsValid(true);
		p.setType(dealerPrice());
		p.setDecidedBy(Code.EDMS);
		p.setDecidedOn(GO_LIVE_TIMESTAMP);
		logger.info("\n    Price: " + p);
		return Arrays.asList(p);
	}

	protected abstract BigDecimal getPricePerCase(EdmsItem e);

	private List<QtyPerUom> toQtyPerUomList(EdmsItem e) {
		return Arrays.asList(perPiece(), perBottle(), perCase(e), perLiter(e), perPallet(e));
	}

	private QtyPerUom perPiece() {
		QtyPerUom q = new QtyPerUom();
		q.setQty(BigDecimal.ONE);
		q.setUom(UomType.EA);
		return q;
	}

	private QtyPerUom perBottle() {
		QtyPerUom q = new QtyPerUom();
		q.setQty(BigDecimal.ONE);
		q.setUom(UomType.BTL);
		q.setSold(true);
		return q;
	}

	private QtyPerUom perCase(EdmsItem e) {
		QtyPerUom q = new QtyPerUom();
		q.setQty(new BigDecimal(getBottlesPerCaseText(e)));
		q.setUom(UomType.CS);
		q.setPurchased(true);
		q.setSold(true);
		q.setReported(true);
		return q;
	}

	private QtyPerUom perLiter(EdmsItem e) {
		QtyPerUom q = new QtyPerUom();
		q.setQty(getVolume(e));
		q.setUom(UomType.L);
		return q;
	}

	private BigDecimal getVolume(EdmsItem e) {
		String s = e.getPackaging();
		s = StringUtils.substringBefore(s, "m");
		BigDecimal ml = new BigDecimal(s);
		return ml.divide(new BigDecimal("1000"), 4, RoundingMode.HALF_EVEN);
	}

	private QtyPerUom perPallet(EdmsItem e) {
		QtyPerUom q = new QtyPerUom();
		q.setQty(new BigDecimal(getCasesPerPalletText(e)));
		q.setUom(UomType.PLT);
		return q;
	}

	@Override
	public List<Item> list() {
		Iterable<EdmsItem> i = edmsItemRepository.findAll();
		return StreamSupport.stream(i.spliterator(), false).map(e -> toDTO(e)).collect(Collectors.toList());
	}

	@Override
	public BigDecimal getBottlesPerCase(EdmsInvoiceDetail d) {
		return getBottlesPerCase(d.getItemCode());
	}

	@Override
	public BigDecimal getBottlesPerCase(EdmsPurchaseReceiptDetail p) {
		return getBottlesPerCase(p.getItemCode());
	}

	@Override
	public BigDecimal getBottlesPerCase(PickListDetail pd) {
		return getBottlesPerCase(pd.getItemVendorNo());
	}

	@Override
	public BigDecimal getBottlesPerCase(BillableDetail bd) {
		return getBottlesPerCase(bd.getItemVendorNo());
	}

	protected abstract BigDecimal getBottlesPerCase(String itemVendorNo);
}