package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.log4j.Logger.getLogger;
import static org.springframework.util.StringUtils.capitalize;
import static ph.txtdis.type.ItemType.PURCHASED;
import static ph.txtdis.type.ScriptType.PRICE_APPROVAL;
import static ph.txtdis.type.ScriptType.VOLUME_DISCOUNT_APPROVAL;
import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;
import static ph.txtdis.util.DateTimeUtils.toHypenatedYearMonthDay;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import ph.txtdis.dto.AbstractDecisionNeeded;
import ph.txtdis.dto.Bom;
import ph.txtdis.dto.Channel;
import ph.txtdis.dto.EntityDecisionNeeded;
import ph.txtdis.dto.Item;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.Keyed;
import ph.txtdis.dto.Price;
import ph.txtdis.dto.PricingType;
import ph.txtdis.dto.QtyPerUom;
import ph.txtdis.dto.VolumeDiscount;
import ph.txtdis.excel.ExcelWriter;
import ph.txtdis.exception.DateInThePastException;
import ph.txtdis.exception.DeactivatedException;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NoVendorIdPurchasedItemException;
import ph.txtdis.exception.NotAPurchasedItemException;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.exception.NotToBeSoldItemException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.type.ItemType;
import ph.txtdis.type.ScriptType;
import ph.txtdis.type.UomType;
import ph.txtdis.type.VolumeDiscountType;
import ph.txtdis.util.ClientTypeMap;

public abstract class AbstractItemService implements ItemService {

	private static Logger logger = getLogger(AbstractItemService.class);

	private static final String DISCOUNT_TAB = "Volume Discount";

	private static final String PRICING_TAB = "Pricing";

	private static final String ITEM = "item";

	@Autowired
	private ExcelWriter excel;

	@Autowired
	private ChannelService channelService;

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ItemFamilyService itemFamilyService;

	@Autowired
	private PricingTypeService pricingTypeService;

	@Autowired
	private ReadOnlyService<Item> readOnlyService;

	@Autowired
	private SavingService<Item> savingService;

	@Autowired
	private ScriptService scriptService;

	@Autowired
	private SpunService<Item, Long> spunService;

	@Autowired
	private RestServerService serverService;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	protected String modulePrefix;

	private List<Item> items;

	private Item item, part;

	private String tab;

	public AbstractItemService() {
		reset();
	}

	@Override
	public boolean canApprove() {
		return credentialService.isUser(MANAGER);
	}

	@Override
	public Bom createBom(BigDecimal qty) {
		Bom b = new Bom();
		b.setPart(part.getName());
		b.setQty(qty);
		return b;
	}

	@Override
	public VolumeDiscount createDiscountUponValidation(VolumeDiscountType type, UomType uom, Integer cutoff,
			BigDecimal discount, Channel channel, LocalDate startDate) throws DateInThePastException, DuplicateException {
		validateStartDate(discounts(), startDate);
		return new VolumeDiscount(type, uom, cutoff, discount, channelService.nullIfAll(channel), startDate);
	}

	@Override
	public Price createPricingUponValidation(PricingType type, BigDecimal price, Channel channel, LocalDate startDate)
			throws DateInThePastException, DuplicateException {
		validateStartDateOfPricingType(prices(), type, startDate);
		return new Price(type, price, channelService.nullIfAll(channel), startDate);
	}

	@Override
	public QtyPerUom createQtyPerUom(UomType uom, BigDecimal qty, Boolean isPurchased, Boolean isSold,
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
	@SuppressWarnings("unchecked")
	public Item find(Long id) throws Exception {
		Item i = findItem(id.toString());
		if (i == null)
			throw new NotFoundException("Item No. " + id);
		if (i.getDeactivatedOn() != null)
			throw new DeactivatedException(i.getName());
		if (i.getType() == PURCHASED && i.getVendorId() == null)
			throw new NoVendorIdPurchasedItemException(i);
		return i;
	}

	private Item findItem(String endPt) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return readOnlyService.module(ITEM).getOne("/" + endPt);
	}

	@Override
	public Item findByName(String name) throws Exception {
		return findItem("find?name=" + name);
	}

	@Override
	public Item findByVendorId(Long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		Item i = getByVendorId(id);
		if (i == null)
			throw new NotFoundException("Vendor Item No. " + id);
		return i;
	}

	protected Item getByVendorId(Long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return findItem("get?vendorId=" + id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Item get() {
		if (item == null)
			reset();
		return item;
	}

	@Override
	public String getAlternateName() {
		return capitalize(ITEM);
	}

	@Override
	public String getCreatedBy() {
		return get().getCreatedBy();
	}

	@Override
	public ZonedDateTime getCreatedOn() {
		return get().getCreatedOn();
	}

	@Override
	public String getDecidedBy() {
		return username();
	}

	@Override
	public ZonedDateTime getDecidedOn() {
		return ZonedDateTime.now();
	}

	@Override
	public String getDescription() {
		return get().getDescription();
	}

	@Override
	public List<Bom> getDetails() {
		List<Bom> l = listBoms();
		logger.info("\n    listBOMs@getDetails = " + l);
		return l == null ? emptyList() : l;
	}

	@Override
	public LocalDate getEndOfLife() {
		return get().getEndOfLife();
	}

	@Override
	public Long getId() {
		return get().getId();
	}

	@Override
	public Boolean getIsValid() {
		return getDeactivatedOn() != null;
	}

	@Override
	public Item getItem() {
		return part;
	}

	@Override
	public ItemService getItemService() {
		return this;
	}

	@Override
	public VolumeDiscount getLatestVolumeDiscount(Channel channel, LocalDate date) {
		try {
			return get().getVolumeDiscounts().stream()//
					.filter(isLatestAndSameChannel(channel, date)).max(VolumeDiscount::compareTo).get();
		} catch (Exception e) {
			return null;
		}
	}

	private Predicate<? super VolumeDiscount> isLatestAndSameChannel(Channel channel, LocalDate date) {
		return vd -> vd.getStartDate().compareTo(date) <= 0
				&& (vd.getChannelLimit() == null || vd.getChannelLimit().equals(channel));
	}

	@Override
	public String getLastModifiedBy() {
		return get().getLastModifiedBy();
	}

	@Override
	public ZonedDateTime getLastModifiedOn() {
		return get().getLastModifiedOn();
	}

	@Override
	public String getModule() {
		return ITEM;
	}

	@Override
	public String getName() {
		return get().getName();
	}

	@Override
	public List<Price> getPriceList() {
		return get().getPriceList();
	}

	@Override
	public BigDecimal getQtyPerUom(Item item, UomType uom) {
		try {
			return item.getQtyPerUomList().stream()//
					.filter(q -> q.getUom() == uom)//
					.findAny().get().getQty();
		} catch (Exception e) {
			return ZERO;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public ReadOnlyService<Item> getReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public String getRemarks() {
		return "";
	}

	@Override
	@SuppressWarnings("unchecked")
	public SavingService<Item> getSavingService() {
		return savingService;
	}

	@Override
	public ScriptService getScriptService() {
		return scriptService;
	}

	@Override
	public <T extends EntityDecisionNeeded<Long>> ScriptType getScriptType(T d) {
		return (d instanceof Price) ? PRICE_APPROVAL : VOLUME_DISCOUNT_APPROVAL;
	}

	@Override
	public SpunService<Item, Long> getSpunService() {
		return spunService;
	}

	@Override
	public String getTitleText() {
		return credentialService.username() + "@" + modulePrefix + " " + ItemService.super.getTitleText();
	}

	@Override
	public ItemType getType() {
		return get().getType();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public String getVendorId() {
		return get().getVendorId();
	}

	@Override
	public List<VolumeDiscount> getVolumeDiscounts() {
		return get().getVolumeDiscounts();
	}

	@Override
	public boolean hasPurchaseUom() {
		return uomExists(q -> q.getPurchased() != null && q.getPurchased() == true);
	}

	@Override
	public boolean hasReportUom() {
		return uomExists(q -> q.getReported() != null && q.getReported() == true);
	}

	@Override
	public boolean hasSoldUom() {
		return uomExists(q -> q.getSold() != null && q.getSold() == true);
	}

	@Override
	public boolean isNew() {
		return getId() == null;
	}

	@Override
	public boolean isNotDiscounted() {
		return get().isNotDiscounted();
	}

	@Override
	public boolean isPurchased() {
		return get().getType() == PURCHASED;
	}

	@Override
	public List<Item> list() {
		return items;
	}

	@Override
	public List<Bom> listBoms() {
		logger.info("\n    get().getBoms@listBoms() = " + get().getBoms());
		return get().getBoms();
	}

	@Override
	public List<UomType> listBuyingUoms(Item item) throws NotAPurchasedItemException {
		List<UomType> uom = filterBoughtUom(item);
		if (uom.isEmpty())
			throw new NotAPurchasedItemException(item);
		return uom;
	}

	@Override
	public List<ItemFamily> listParents() {
		if (isNew())
			try {
				return itemFamilyService.listItemParents();
			} catch (Exception e) {
				return null;
			}
		return asList(get().getFamily());
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
	public List<QtyPerUom> listQtyPerUom() {
		return get().getQtyPerUomList();
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
	public List<UomType> listSellingUoms(Item item) throws NotToBeSoldItemException {
		List<UomType> uom = filterSoldUom(item);
		if (uom.isEmpty())
			throw new NotToBeSoldItemException(item);
		return uom;
	}

	@Override
	public List<ItemType> listTypes() {
		return asList(ItemType.values());
	}

	@Override
	public List<UomType> listUoms() {
		return listQtyPerUom() == null ? asList(UomType.EA) : unusedUoms();
	}

	@Override
	public boolean noChangesNeedingApproval(String tab) {
		this.tab = tab;
		if (tab.equals(PRICING_TAB))
			return noChanges(get().getPriceList());
		if (tab.equals(DISCOUNT_TAB))
			return noChanges(get().getVolumeDiscounts());
		return true;
	}

	@Override
	public void reset() {
		set(new Item());
		items = null;
		part = null;
	}

	@Override
	public void save() throws SuccessfulSaveInfo, NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, RestException {
		try {
			set(save(get()));
		} catch (Exception e) {
			logger.info("\n    Exception@save(get()) = " + e.getMessage());
			e.printStackTrace();
			throw new InvalidException(e.getMessage());
		}
		scriptService.saveScripts();
		throw new SuccessfulSaveInfo(getModuleId() + getOrderNo());
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveAsExcel(AppTable<Item>... tables) throws IOException {
		excel.filename(excelName()).sheetname(getExcelSheetName()).table(tables).write();
	}

	private String excelName() {
		return getExcelSheetName() + "." + toHypenatedYearMonthDay(now());
	}

	@Override
	public List<Item> search(String text) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		String endPt = text.isEmpty() ? "" : "/search?name=" + text;
		return items = findItems(endPt);
	}

	protected List<Item> findItems(String endPt) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return readOnlyService.module(ITEM).getList(endPt);
	}

	@Override
	public <T extends Keyed<Long>> void set(T t) {
		item = (Item) t;
	}

	@Override
	public void setBoms(List<Bom> l) {
		get().setBoms(l);
	}

	@Override
	public void setEndOfLife(LocalDate d) {
		get().setEndOfLife(d);
	}

	@Override
	public void setFamily(ItemFamily f) {
		get().setFamily(f);
	}

	@Override
	public void setNameIfUnique(String text) throws Exception {
		if (isNew() && isOffSite())
			throw new NotAllowedOffSiteTransactionException();
		if (findByName(text) != null)
			throw new DuplicateException(text);
		get().setName(text);
	}

	@Override
	public void setNotDiscounted(boolean b) {
		get().setNotDiscounted(b);
	}

	@Override
	public void setPartUponValidation(Long id) throws Exception {
		if (id != 0) {
			part = null;
			part = verifyItem(id);
		}
	}

	@Override
	public void setPriceList(List<Price> items) {
		get().setPriceList(items);
	}

	@Override
	public void setType(ItemType t) {
		get().setType(t);
	}

	@Override
	public void setVolumeDiscounts(List<VolumeDiscount> discounts) {
		get().setVolumeDiscounts(discounts);
	}

	@Override
	public void updatePerValidity(Boolean isValid, String remarks) {
		if (tab.equals(PRICING_TAB))
			approvePricing(isValid, remarks);
		if (tab.equals(DISCOUNT_TAB))
			approveDiscount(isValid, remarks);
	}

	@Override
	public void validatePurchasedUom(Boolean b) throws DuplicateException {
		if (isNewQtyPerUom())
			return;
		if (hasPurchaseUom())
			throw new DuplicateException("A purchase UOM");
	}

	@Override
	public void validateReportedUom(Boolean value) throws DuplicateException {
		if (isNewQtyPerUom())
			return;
		if (hasReportUom())
			throw new DuplicateException("A report UOM");
	}

	private void approveDiscount(Boolean isValid, String remarks) {
		List<VolumeDiscount> list = approve(get().getVolumeDiscounts(), isValid, remarks);
		get().setVolumeDiscounts(list);
	}

	private void approvePricing(Boolean isValid, String remarks) {
		List<Price> l = approve(get().getPriceList(), isValid, remarks);
		get().setPriceList(l);
	}

	private List<VolumeDiscount> discounts() {
		if (get().getVolumeDiscounts() == null)
			get().setVolumeDiscounts(new ArrayList<>());
		return get().getVolumeDiscounts();
	}

	private List<UomType> filterBoughtUom(Item item) {
		List<QtyPerUom> qpu = item.getQtyPerUomList();
		return qpu == null ? new ArrayList<>()
				: qpu.stream().filter(q -> q.getPurchased() != null && q.getPurchased()).map(q -> q.getUom())
						.collect(toList());
	}

	private List<UomType> filterSoldUom(Item item) {
		List<QtyPerUom> qpu = item.getQtyPerUomList();
		return qpu == null ? new ArrayList<>()
				: qpu.stream().filter(q -> q.getSold() != null && q.getSold()).map(q -> q.getUom()).collect(toList());
	}

	private String getExcelSheetName() {
		return "Active.Items";
	}

	private boolean isNewQtyPerUom() {
		return listQtyPerUom() == null || listQtyPerUom().isEmpty();
	}

	private <T extends AbstractDecisionNeeded<Long>> boolean noChanges(List<T> l) {
		return l == null ? true : !l.stream().anyMatch(d -> d.getIsValid() == null);
	}

	private List<Price> prices() {
		if (get().getPriceList() == null)
			get().setPriceList(new ArrayList<>());
		return get().getPriceList();
	}

	private boolean startDateForPaymentTypeExists(List<Price> list, PricingType type, LocalDate startDate) {
		return list.stream().anyMatch(r -> r.getStartDate().equals(startDate) && r.getType().equals(type));
	}

	private List<UomType> unusedUoms() {
		List<UomType> l = new ArrayList<>(asList(UomType.values()));
		l.removeAll(listQtyPerUom().stream().map(QtyPerUom::getUom).collect(toList()));
		return l;
	}

	private boolean uomExists(Predicate<QtyPerUom> p) {
		return listQtyPerUom().stream().anyMatch(p);
	}

	private void validateStartDateOfPricingType(List<Price> prices, PricingType type, LocalDate startDate)
			throws DateInThePastException, DuplicateException {
		confirmDateIsNotInThePast(startDate);
		if (startDateForPaymentTypeExists(prices, type, startDate))
			throw new DuplicateException(toDateDisplay(startDate) + " for " + type);
	}

	@Override
	public boolean isOffSite() {
		return serverService.isOffSite();
	}

	@Override
	public ReadOnlyService<Item> getListedReadOnlyService() {
		return readOnlyService;
	}

	@Override
	public void setDescription(String text) {
		get().setDescription(text);
	}

	@Override
	public void setId(Long id) {
		get().setId(id);
	}

	@Override
	public void setName(String text) {
		get().setName(text);
	}

	@Override
	public void setQtyPerUomList(List<QtyPerUom> list) {
		get().setQtyPerUomList(list);
	}

	@Override
	public void setVendorId(String id) {
		get().setVendorId(id);
	}

	@Override
	public String username() {
		return credentialService.username();
	}
}
