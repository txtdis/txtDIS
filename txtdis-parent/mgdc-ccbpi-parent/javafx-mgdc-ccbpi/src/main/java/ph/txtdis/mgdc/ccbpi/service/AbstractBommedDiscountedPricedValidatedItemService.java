package ph.txtdis.mgdc.ccbpi.service;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.ItemType.FREE;
import static ph.txtdis.type.ItemType.PURCHASED;
import static ph.txtdis.util.DateTimeUtils.getServerDate;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;
import static ph.txtdis.util.DateTimeUtils.toHypenatedYearMonthDay;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.AbstractDecisionNeeded;
import ph.txtdis.dto.Bom;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.Price;
import ph.txtdis.dto.PricingType;
import ph.txtdis.dto.QtyPerUom;
import ph.txtdis.excel.ExcelReportWriter;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.NotAPurchasedItemException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.exception.NotToBeSoldItemException;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.mgdc.ccbpi.dto.Channel;
import ph.txtdis.mgdc.ccbpi.dto.Item;
import ph.txtdis.mgdc.service.PricingTypeService;
import ph.txtdis.service.SyncService;
import ph.txtdis.type.ItemType;
import ph.txtdis.type.UomType;

public abstract class AbstractBommedDiscountedPricedValidatedItemService //
	extends AbstractItemService //
	implements BommedDiscountedPricedValidatedItemService {

	private static final String PRICING_TAB = "Pricing";

	private static Logger logger = getLogger(AbstractBommedDiscountedPricedValidatedItemService.class);

	@Autowired
	protected PricingTypeService pricingTypeService;

	@Autowired
	private ExcelReportWriter excel;

	private Item part;

	private String tab;

	@Override
	public Bom createBom(BigDecimal qty) {
		Bom b = new Bom();
		b.setPart(part.getName());
		b.setQty(qty);
		return b;
	}

	@Override
	public Price createPricingUponValidation(PricingType type, BigDecimal price, Channel channel, LocalDate startDate)
		throws Exception {
		if (price == null || startDate == null)
			return null;
		validateStartDateOfPricingType(getPriceList(), type, startDate);
		return newPrice(type, price, startDate);
	}

	private void validateStartDateOfPricingType(List<Price> prices, PricingType type, LocalDate startDate)
		throws Exception {
		// confirmDateIsNotInThePast(startDate);
		if (startDateForPaymentTypeExists(prices, type, startDate))
			throw new DuplicateException(toDateDisplay(startDate) + " for " + type);
	}

	@Override
	public List<Price> getPriceList() {
		return get().getPriceList();
	}

	private Price newPrice(PricingType type, BigDecimal price, LocalDate startDate) {
		Price p = new Price();
		p.setType(type);
		p.setPriceValue(price);
		p.setStartDate(startDate);
		return p;
	}

	private boolean startDateForPaymentTypeExists(List<Price> list, PricingType type, LocalDate startDate) {
		try {
			return list.stream().anyMatch(r -> r.getStartDate().equals(startDate) && r.getType().equals(type));
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void setPriceList(List<Price> items) {
		get().setPriceList(items);
	}

	@Override
	public QtyPerUom createQtyPerUom(UomType uom,
	                                 BigDecimal qty,
	                                 Boolean isPurchased,
	                                 Boolean isSold,
	                                 Boolean isReported) {
		QtyPerUom q = new QtyPerUom();
		q.setUom(uom);
		q.setQty(qty);
		q.setPurchased(isPurchased);
		q.setSold(isSold);
		q.setReported(isReported);
		return q;
	}

	@Override
	public Item findByVendorNo(String id) throws Exception {
		Item i = findItem("get?vendorId=" + id);
		if (i == null)
			throw new NotFoundException("Vendor Item No. " + id);
		return i;
	}

	@Override
	public String getDescription() {
		return get().getDescription();
	}

	@Override
	public void setDescription(String text) {
		get().setDescription(text);
	}

	@Override
	public List<Bom> getDetails() {
		List<Bom> l = listBoms();
		logger.info("\n    listBOMs@getDetails = " + l);
		return l == null ? emptyList() : l;
	}

	@Override
	public List<Bom> listBoms() {
		return get().getBoms();
	}

	@Override
	public LocalDate getEndOfLife() {
		return get().getEndOfLife();
	}

	@Override
	public void setEndOfLife(LocalDate d) {
		get().setEndOfLife(d);
	}

	@Override
	public Item getItem() {
		return part;
	}

	@Override
	public BommedDiscountedPricedValidatedItemService getItemService() {
		return this;
	}

	@Override
	public String getName() {
		return get().getName();
	}

	@Override
	public void setName(String text) {
		get().setName(text);
	}

	@Override
	public BigDecimal getQtyPerUom(Item item, UomType uom) {
		try {
			return item.getQtyPerUomList().stream() //
				.filter(q -> q.getUom() == uom) //
				.findAny().get().getQty();
		} catch (Exception e) {
			return ZERO;
		}
	}

	@Override
	public String getVendorNo() {
		return get().getVendorNo();
	}

	@Override
	public boolean hasSoldUom() {
		return uomExists(q -> q.getSold() != null && q.getSold() == true);
	}

	private boolean uomExists(Predicate<QtyPerUom> p) {
		return listQtyPerUom().stream().anyMatch(p);
	}

	@Override
	public List<QtyPerUom> listQtyPerUom() {
		return get().getQtyPerUomList();
	}

	@Override
	public boolean isNotDiscounted() {
		return get().isNotDiscounted();
	}

	@Override
	public void setNotDiscounted(boolean b) {
		get().setNotDiscounted(b);
	}

	@Override
	public boolean isPurchased() {
		return getType() == PURCHASED;
	}

	@Override
	public ItemType getType() {
		return get().getType();
	}

	@Override
	public void setType(ItemType t) {
		get().setType(t);
	}

	@Override
	public boolean isSold() {
		return getType() != FREE;
	}

	@Override
	public List<UomType> listBuyingUoms(Item i) throws Exception {
		List<UomType> uom = filterBoughtUom(i);
		if (uom.isEmpty())
			throw new NotAPurchasedItemException(i.getName());
		return uom;
	}

	private List<UomType> filterBoughtUom(Item item) {
		List<QtyPerUom> qpu = item.getQtyPerUomList();
		return qpu == null ? new ArrayList<>()
			:
			qpu.stream().filter(q -> q.getPurchased() != null && q.getPurchased()).map(q -> q.getUom()).collect(toList());
	}

	@Override
	public List<PricingType> listPricingTypes() {
		try {
			return pricingTypeService.list();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<UomType> listSellingUoms() {
		try {
			return listSellingUoms(item);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<UomType> listSellingUoms(Item i) throws Exception {
		List<UomType> uom = filterSoldUom(i);
		if (uom.isEmpty())
			throw new NotToBeSoldItemException(i.getName());
		return uom;
	}

	private List<UomType> filterSoldUom(Item item) {
		List<QtyPerUom> qpu = item.getQtyPerUomList();
		return qpu == null ? new ArrayList<>() :
			qpu.stream().filter(q -> q.getSold() != null && q.getSold()).map(q -> q.getUom()).collect(toList());
	}

	@Override
	public List<ItemType> listTypes() {
		return asList(ItemType.values());
	}

	@Override
	public List<UomType> listUoms() {
		return listQtyPerUom() == null ? asList(UomType.EA) : unusedUoms();
	}

	private List<UomType> unusedUoms() {
		List<UomType> l = new ArrayList<>(asList(UomType.values()));
		l.removeAll(listQtyPerUom().stream().map(QtyPerUom::getUom).collect(toList()));
		return l;
	}

	@Override
	public boolean noChangesNeedingApproval(String tab) {
		this.tab = tab;
		if (tab.equals(PRICING_TAB))
			return noChanges(getPriceList());
		return true;
	}

	private <T extends AbstractDecisionNeeded<Long>> boolean noChanges(List<T> l) {
		return l == null ? true : !l.stream().anyMatch(d -> d.getIsValid() == null);
	}

	@Override
	public void reset() {
		super.reset();
		part = null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<Item>... tables) throws Exception {
		excel.table(tables).filename(excelName()).sheetname(getExcelSheetName()).write();
	}

	private String excelName() {
		return getExcelSheetName() + "." + toHypenatedYearMonthDay(today());
	}

	private String getExcelSheetName() {
		return "Active.Items";
	}

	@Override
	public LocalDate today() {
		return getServerDate();
	}

	@Override
	public void setBoms(List<Bom> l) {
		get().setBoms(l);
	}

	@Override
	public void setFamily(ItemFamily f) {
		get().setFamily(f);
	}

	@Override
	public void setNameIfUnique(String text) throws Exception {
		if (findByName(text) != null)
			throw new DuplicateException(text);
		get().setName(text);
	}

	@Override
	public void setPartUponValidation(Long id) throws Exception {
		if (id != 0) {
			part = null;
			part = verifyItem(id);
		}
	}

	@Override
	public void setQtyPerUomList(List<QtyPerUom> list) {
		get().setQtyPerUomList(list);
	}

	@Override
	public void setVendorId(String id) {
		get().setVendorNo(id);
	}

	@Override
	public void updatePerValidity(Boolean isValid, String remarks) {
		if (tab.equals(PRICING_TAB))
			approvePriceList(isValid, remarks);
	}

	private void approvePriceList(Boolean isValid, String remarks) {
		List<Price> l = approve(getPriceList(), isValid, remarks);
		logger.info("\n    ApprovedPriceList = " + l);
		get().setPriceList(l);
	}

	@Override
	public void validatePurchasedUom(Boolean b) throws DuplicateException {
		if (isNewQtyPerUom())
			return;
		if (hasPurchaseUom())
			throw new DuplicateException("A purchase UOM");
	}

	private boolean isNewQtyPerUom() {
		return listQtyPerUom() == null || listQtyPerUom().isEmpty();
	}

	@Override
	public boolean hasPurchaseUom() {
		try {
			return uomExists(q -> q.getPurchased() != null && q.getPurchased() == true);
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void validateReportedUom(Boolean value) throws DuplicateException {
		if (isNewQtyPerUom())
			return;
		if (hasReportUom())
			throw new DuplicateException("A report UOM");
	}

	@Override
	public boolean hasReportUom() {
		return uomExists(q -> q.getReported() != null && q.getReported() == true);
	}
}
