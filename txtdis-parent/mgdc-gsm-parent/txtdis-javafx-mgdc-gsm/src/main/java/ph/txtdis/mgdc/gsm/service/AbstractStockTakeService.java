package ph.txtdis.mgdc.gsm.service;

import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.type.UserType.STOCK_CHECKER;
import static ph.txtdis.type.UserType.STOCK_TAKER;
import static ph.txtdis.type.UserType.STORE_KEEPER;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import ph.txtdis.dto.Keyed;
import ph.txtdis.dto.StockTake;
import ph.txtdis.dto.StockTakeDetail;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.mgdc.service.HolidayService;
import ph.txtdis.service.CredentialService;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.service.SavingService;
import ph.txtdis.service.SpunKeyedService;
import ph.txtdis.service.SyncService;
import ph.txtdis.service.UserService;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;
import ph.txtdis.util.ClientTypeMap;
import ph.txtdis.util.DateTimeUtils;

public abstract class AbstractStockTakeService //
		implements StockTakeService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private HolidayService holidayService;

	@Autowired
	private BommedDiscountedPricedValidatedItemService itemService;

	@Autowired
	private SavingService<StockTake> savingService;

	@Autowired
	private SpunKeyedService<StockTake, Long> spunService;

	@Autowired
	private SyncService syncService;

	@Autowired
	private UserService userService;

	@Autowired
	private WarehouseService warehouseService;

	@Autowired
	private ReadOnlyService<StockTake> stockTakeReadOnlyService;

	@Autowired
	private ClientTypeMap typeMap;

	@Value("${prefix.module}")
	private String modulePrefix;

	private BigDecimal quantity;

	private Item item;

	private List<String> warehouses, takers, checkers;

	private QualityType quality;

	private StockTake stockTake;

	private UomType uom;

	public AbstractStockTakeService() {
		reset();
	}

	@Override
	public StockTakeDetail createDetail() {
		StockTakeDetail d = new StockTakeDetail();
		d.setId(item.getId());
		d.setName(item.getName());
		d.setQty(quantity);
		d.setQuality(quality);
		d.setUom(uom);
		d.setQtyPerCase(itemService.getQtyPerUom(item, uom).intValue());
		return d;
	}

	@Override
	public StockTake openByDate(String dateText) throws Exception {
		LocalDate date = DateTimeUtils.toDate(dateText);
		return stockTakeReadOnlyService.module(getModuleName()).getOne("/date?on=" + date);
	}

	@Override
	@SuppressWarnings("unchecked")
	public StockTake get() {
		if (stockTake == null)
			set(new StockTake());
		return stockTake;
	}

	@Override
	public String getAlternateName() {
		return "Stock Take";
	}

	@Override
	public StockTake getByBooking(Long id) throws Exception {
		return stockTakeReadOnlyService.module(getModuleName()).getOne("/booking?id=" + id);
	}

	@Override
	public LocalDate getCountDate() {
		if (get().getCountDate() == null)
			get().setCountDate(syncService.getServerDate());
		return get().getCountDate();
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
	public List<StockTakeDetail> getDetails() {
		return get().getDetails();
	}

	@Override
	public String getHeaderName() {
		return getAlternateName();
	}

	@Override
	public Long getId() {
		return get().getId();
	}

	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public BommedDiscountedPricedValidatedItemService getItemService() {
		return itemService;
	}

	@Override
	public StockTake getLatestCount() {
		try {
			return stockTakeReadOnlyService.module(getModuleName()).getOne("/count?warehouse=all&date=" + syncService.getServerDate());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public LocalDate getLatestCountDate() {
		try {
			return getLatestCount().getCountDate();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getModuleName() {
		return "stockTake";
	}

	@Override
	public StockTake getPreviousCount() {
		try {
			return stockTakeReadOnlyService.module(getModuleName())
					.getOne("/count?warehouse=all&date=" + holidayService.previousWorkDay(getLatestCountDate()));
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public LocalDate getPreviousCountDate() {
		try {
			return getPreviousCount().getCountDate();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public ReadOnlyService<StockTake> getReadOnlyService() {
		return stockTakeReadOnlyService;
	}

	@Override
	public String getRemarks() {
		return get().getRemarks();
	}

	@Override
	@SuppressWarnings("unchecked")
	public SavingService<StockTake> getSavingService() {
		return savingService;
	}

	@Override
	public SpunKeyedService<StockTake, Long> getSpunService() {
		return spunService;
	}

	@Override
	public String getTitleName() {
		return credentialService.username() + "@" + modulePrefix + " " + StockTakeService.super.getTitleName();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public boolean isUserAStockTaker() {
		return credentialService.isUser(STOCK_TAKER) || credentialService.isUser(MANAGER);
	}

	@Override
	public boolean isCountToday() {
		return getCountDate().equals(syncService.getServerDate());
	}

	@Override
	public List<String> listCheckers() {
		return isNew() ? allCheckers() : getActualCheckerAsList();
	}

	private List<String> allCheckers() {
		if (checkers == null)
			checkers = userService.listNamesByRole(STORE_KEEPER, STOCK_CHECKER);
		return checkers;
	}

	private List<String> getActualCheckerAsList() {
		String s = get().getChecker();
		return s == null ? null : Arrays.asList(s);
	}

	@Override
	public List<String> listTakers() {
		return isNew() ? allTakers() : getActualTakerAsList();
	}

	private List<String> allTakers() {
		if (takers == null)
			takers = userService.listNamesByRole(STOCK_TAKER);
		return takers;
	}

	private List<String> getActualTakerAsList() {
		String s = get().getTaker();
		return s == null ? null : Arrays.asList(s);
	}

	@Override
	public List<String> listWarehouses() {
		return isNew() ? allWarehouses() : getWarehouseAsList();
	}

	private List<String> allWarehouses() {
		if (warehouses == null)
			warehouses = warehouseService.listNames();
		return warehouses;
	}

	private List<String> getWarehouseAsList() {
		String s = get().getWarehouse();
		return s == null ? null : Arrays.asList(s);
	}

	@Override
	public void reset() {
		checkers = null;
		item = null;
		quality = null;
		quantity = null;
		stockTake = null;
		takers = null;
		warehouses = null;
		uom = null;
	}

	@Override
	public <T extends Keyed<Long>> void set(T t) {
		stockTake = (StockTake) t;
	}

	@Override
	public void setChecker(String checkerName) {
		get().setChecker(checkerName);
	}

	@Override
	public void setDetails(List<StockTakeDetail> details) {
		get().setDetails(details);
	}

	@Override
	public void setItemUponValidation(long id) throws Exception {
		item = null;
		item = confirmItemExistsAndIsNotDeactivated(id);
	}

	@Override
	public void setTaker(String takerName) {
		get().setTaker(takerName);
	}

	@Override
	public String getItemName() {
		return StockTakeService.super.getItemName();
	}

	@Override
	public List<String> listItemsOnStock() throws Exception {
		StockTake t = spunService.module(getModuleName()).previous(0L);
		return t == null ? null //
				: t.getDetails().stream() //
						.map(d -> toItem(d)) //
						.filter(i -> i != null) //
						.distinct() //
						.sorted((a, b) -> a.getId().compareTo(b.getId())) //
						.map(i -> i.getName())//
						.collect(Collectors.toList());
	}

	private Item toItem(StockTakeDetail d) {
		try {
			return itemService.findByName(d.getName());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void setId(Long id) {
		get().setId(id);
	}

	@Override
	public void setQtyUponValidation(UomType uom, BigDecimal qty) throws Exception {
		this.uom = uom;
		this.quantity = qty;
	}

	@Override
	public void setQuality(QualityType quality) {
		this.quality = quality;
	}

	@Override
	public BigDecimal getQtyPerUom(UomType uom) {
		return itemService.getQtyPerUom(item, uom);
	}

	@Override
	public void setRemarks(String text) {
		get().setRemarks(text);
	}

	@Override
	public void setWarehouseIfAllCountDateTransactionsAreCompleteAndNoStockTakeAlreadyMadeOnCountDate(String warehouse) throws Exception {
		if (!isNew() || getCountDate() == null)
			return;
		verifyNoStockTakeMadeOnWarehouse(warehouse);
		verifyAllCountDateTransactionsAreComplete();
		get().setWarehouse(warehouse);
	}

	protected abstract void verifyAllCountDateTransactionsAreComplete() throws Exception;

	private void verifyNoStockTakeMadeOnWarehouse(String warehouse) throws Exception {
		StockTake s = stockTakeReadOnlyService.module(getModuleName()).getOne("/count?warehouse=" + warehouse + "&date=" + getCountDate());
		if (s != null)
			throw new DuplicateException(warehouse + " stock take on\n" + getCountDate());
	}
}
