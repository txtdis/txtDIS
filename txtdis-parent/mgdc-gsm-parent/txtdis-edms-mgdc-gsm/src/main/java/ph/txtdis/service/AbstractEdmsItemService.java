package ph.txtdis.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ph.txtdis.domain.EdmsInvoiceDetail;
import ph.txtdis.domain.EdmsItem;
import ph.txtdis.domain.EdmsPurchaseReceiptDetail;
import ph.txtdis.domain.EdmsSalesOrderDetail;
import ph.txtdis.dto.*;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.repository.EdmsItemRepository;
import ph.txtdis.type.ItemType;
import ph.txtdis.type.PriceType;
import ph.txtdis.type.UomType;
import ph.txtdis.util.Code;
import ph.txtdis.util.DateTimeUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.math.BigDecimal.ONE;
import static java.math.RoundingMode.HALF_EVEN;
import static java.util.Comparator.comparing;
import static java.util.stream.StreamSupport.stream;
import static ph.txtdis.type.PriceType.DEALER;
import static ph.txtdis.type.UomType.*;
import static ph.txtdis.util.Code.LIQUOR;
import static ph.txtdis.util.NumberUtils.divide;

public abstract class AbstractEdmsItemService //
	implements EdmsItemService {

	private static final LocalDate GO_LIVE_DATE = LocalDate.of(2016, 3, 15);

	private static final ZonedDateTime GO_LIVE_TIMESTAMP = GO_LIVE_DATE.atStartOfDay(ZoneId.systemDefault());

	@Autowired
	private EdmsItemRepository edmsItemRepository;

	@Autowired
	private EdmsItemFamilyService familyService;

	@Value("${client.user}")
	private String username;

	@Override
	public EdmsItem getItem(ItemVendorNo d) {
		return edmsItemRepository.findByCodeIgnoreCase(d.getItemVendorNo());
	}

	@Override
	public Item save(Item i) {
		EdmsItem e = edmsItemRepository.findByCodeIgnoreCase(i.getVendorNo());
		edmsItemRepository.save(e == null ? createItem(i) : updateItem(e, i));
		return i;
	}

	protected abstract EdmsItem createItem(Item i);

	private EdmsItem updateItem(EdmsItem e, Item i) {
		updatePrices(i);
		if (deactivated(i))
			e.setIsActive(false);
		return e;
	}

	private void updatePrices(Item i) {
		updatePricePerBottle(i);
		updatePricePerCase(i);
	}

	private boolean deactivated(Item i) {
		return i.getDeactivatedOn() != null;
	}

	protected abstract BigDecimal updatePricePerBottle(Item i);

	protected abstract void updatePricePerCase(Item i);

	protected EdmsItem toEdmsItem(Item i) {
		EdmsItem e = new EdmsItem();
		e.setCode(i.getVendorNo());
		e.setName(i.getName());
		e.setPackaging(getPackaging(i));
		e.setClazz(LIQUOR);
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
		e.setCreatedBy(username);
		e.setCreatedOn(DateTimeUtils.toTimestampText(i.getCreatedOn()));
		e = updateItem(e, i);
		return e;
	}

	private String getPackaging(Item i) {
		return getVolume(i) + "mL x " + getBottlesPerCase(i) + " x " + getCasesPerPallet(i) + "P";
	}

	private int getVolume(Item item) {
		return getQtyPer(item, UomType.L).multiply(new BigDecimal("1000")).intValue();
	}

	protected BigDecimal getBottlesPerCase(Item item) {
		return getQtyPer(item, UomType.CS);
	}

	private BigDecimal getCasesPerPallet(Item item) {
		return getQtyPer(item, UomType.PLT);
	}

	private BigDecimal getQtyPer(Item item, UomType uom) {
		try {
			return item.getQtyPerUomList().stream()//
				.filter(q -> q.getUom() == uom)//
				.findAny().get().getQty();
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}

	protected BigDecimal getPricePerBottle(Item i) {
		try {
			BigDecimal price = i.getPriceList().stream()//
				.filter(p -> p.getType().getName().equalsIgnoreCase(DEALER.toString()) //
					&& p.getIsValid() != null && p.getIsValid() //
					&& !p.getStartDate().isAfter(LocalDate.now()))
				.max(comparing(Price::getStartDate))//
				.get().getPriceValue();
			return divide(price, getBottlesPerCase(i));
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}

	@Override
	public BigDecimal getPricePerCase(EdmsSalesOrderDetail d) {
		return getPricePerCase(d.getItemCode());
	}

	protected abstract BigDecimal getPricePerCase(String itemCode);

	@Override
	public BigDecimal getPricePerCase(EdmsInvoiceDetail i) {
		return getPricePerCase(i.getItemCode());
	}

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
	public List<Item> list() {
		Iterable<EdmsItem> i = edmsItemRepository.findAll();
		return stream(i.spliterator(), false).map(e -> toDTO(e)).collect(Collectors.toList());
	}

	private Item toDTO(EdmsItem e) {
		Item i = new Item();
		i.setName(fitName(e.getName()));
		i.setDescription(fitDescription(e));
		i.setFamily(familyService.toBrand(e.getBrand()));
		i.setPriceList(toPriceList(e));
		i.setQtyPerUomList(toQtyPerUomList(e));
		i.setType(ItemType.PURCHASED);
		i.setVendorNo(e.getCode());
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

	private List<Price> toPriceList(EdmsItem e) {
		Price p = new Price();
		p.setPriceValue(getPricePerCase(e));
		p.setStartDate(GO_LIVE_DATE);
		p.setIsValid(true);
		p.setType(dealerPrice());
		p.setDecidedBy(Code.EDMS);
		p.setDecidedOn(GO_LIVE_TIMESTAMP);
		return Arrays.asList(p);
	}

	private List<QtyPerUom> toQtyPerUomList(EdmsItem e) {
		return Arrays.asList(perPiece(), perBottle(), perCase(e), perLiter(e), perPallet(e));
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

	protected abstract BigDecimal getPricePerCase(EdmsItem e);

	private PricingType dealerPrice() {
		PricingType p = new PricingType();
		p.setName(PriceType.DEALER.toString());
		return p;
	}

	private QtyPerUom perPiece() {
		QtyPerUom q = new QtyPerUom();
		q.setQty(ONE);
		q.setUom(EA);
		return q;
	}

	private QtyPerUom perBottle() {
		QtyPerUom q = new QtyPerUom();
		q.setQty(ONE);
		q.setUom(BTL);
		q.setSold(true);
		return q;
	}

	private QtyPerUom perCase(EdmsItem e) {
		QtyPerUom q = new QtyPerUom();
		q.setQty(new BigDecimal(getBottlesPerCaseText(e)));
		q.setUom(CS);
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

	private QtyPerUom perPallet(EdmsItem e) {
		QtyPerUom q = new QtyPerUom();
		q.setQty(new BigDecimal(getCasesPerPalletText(e)));
		q.setUom(PLT);
		return q;
	}

	private BigDecimal getVolume(EdmsItem e) {
		String s = e.getPackaging();
		s = StringUtils.substringBefore(s, "m");
		BigDecimal ml = new BigDecimal(s);
		return ml.divide(new BigDecimal("1000"), 4, HALF_EVEN);
	}

	@Override
	public BigDecimal getBottlesPerCase(EdmsInvoiceDetail d) {
		return getBottlesPerCase(d.getItemCode());
	}

	protected abstract BigDecimal getBottlesPerCase(String itemVendorNo);

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
}