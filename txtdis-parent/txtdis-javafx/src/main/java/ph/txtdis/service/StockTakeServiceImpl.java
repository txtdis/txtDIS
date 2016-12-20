package ph.txtdis.service;

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
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.dto.Item;
import ph.txtdis.dto.Keyed;
import ph.txtdis.dto.StockTake;
import ph.txtdis.dto.StockTakeDetail;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;
import ph.txtdis.util.ClientTypeMap;

@Service("stockTakeService")
public class StockTakeServiceImpl implements BilledAllPickedSalesOrder, PickedLoadOrderVerified, StockTakeService {

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private ReadOnlyService<Billable> billableReadOnlyService;

	@Autowired
	private ReadOnlyService<StockTake> stockTakeReadOnlyService;

	@Autowired
	private RestServerService serverService;

	@Autowired
	private SavingService<StockTake> savingService;

	@Autowired
	private SpunService<StockTake, Long> spunService;

	@Autowired
	private SyncService syncService;

	@Autowired
	private UserService userService;

	@Autowired
	private WarehouseService warehouseService;

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

	public StockTakeServiceImpl() {
		reset();
	}

	@Override
	public boolean isOffSite() {
		return serverService.isOffSite();
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
	@SuppressWarnings("unchecked")
	public StockTake get() {
		if (stockTake == null)
			reset();
		return stockTake;
	}

	@Override
	public String getAlternateName() {
		return "Stock Take";
	}

	@Override
	public ReadOnlyService<Billable> getBillableReadOnlyService() {
		return billableReadOnlyService;
	}

	@Override
	public StockTake getByBooking(Long id) throws Exception {
		return stockTakeReadOnlyService.module(getModule()).getOne("/booking?id=" + id);
	}

	@Override
	public LocalDate getCountDate() {
		if (get().getCountDate() == null)
			get().setCountDate(syncService.getServerDate());
		return get().getCountDate();
	}

	@Override
	public String getCreatedBy() {
		return stockTake.getCreatedBy();
	}

	@Override
	public ZonedDateTime getCreatedOn() {
		return stockTake.getCreatedOn();
	}

	@Override
	public List<StockTakeDetail> getDetails() {
		return get().getDetails();
	}

	@Override
	public String getHeaderText() {
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
	public ItemService getItemService() {
		return itemService;
	}

	@Override
	public StockTake getLatestCount() {
		try {
			return stockTakeReadOnlyService.module(getModule())
					.getOne("/count?warehouse=all&date=" + syncService.getServerDate());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public LocalDate getLatestCountDate() {
		try {
			return getLatestCount().getCountDate();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getModule() {
		return "stockTake";
	}

	@Override
	public StockTake getPreviousCount() {
		try {
			return stockTakeReadOnlyService.module(getModule())
					.getOne("/count?warehouse=all&date=" + getLatestCountDate().minusDays(1L));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public LocalDate getPreviousCountDate() {
		try {
			return getPreviousCount().getCountDate();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public ReadOnlyService<StockTake> getReadOnlyService() {
		return stockTakeReadOnlyService;
	}

	@Override
	@SuppressWarnings("unchecked")
	public SavingService<StockTake> getSavingService() {
		return savingService;
	}

	@Override
	public SpunService<StockTake, Long> getSpunService() {
		return spunService;
	}

	@Override
	public String getTitleText() {
		return credentialService.username() + "@" + modulePrefix + " " + StockTakeService.super.getTitleText();
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
	public void next() throws Exception {
		set(spunService.module(getModule()).next(getSpunId()));
	}

	@Override
	public void previous() throws Exception {
		set(spunService.module(getModule()).previous(getSpunId()));
	}

	@Override
	public void reset() {
		set(new StockTake());
		instantiateLists();
		nullifyFields();
	}

	private void instantiateLists() {
		takers = null;
		warehouses = null;
		checkers = null;
	}

	private void nullifyFields() {
		uom = null;
		quality = null;
		quantity = null;
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
		return spunService.module(getModule()).previous(null) //
				.getDetails().stream() //
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
			e.printStackTrace();
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
	public void setWarehouseIfAllCountDateTransactionsAreCompleteAndNoStockTakeAlreadyMadeOnCountDate(String warehouse)
			throws Exception {
		if (!isNew())
			return;
		verifyNoStockTakeMadeOnWarehouse(warehouse);
		verifyAllPickedLoadOrdersHaveNoItemQuantityVariances("ALL", getCountDate());
		verifyAllPickedSalesOrderHaveBeenBilled("ALL", getCountDate());
		get().setWarehouse(warehouse);
	}

	private void verifyNoStockTakeMadeOnWarehouse(String warehouse) throws Exception {
		StockTake s = stockTakeReadOnlyService.module(getModule())
				.getOne("/count?warehouse=" + warehouse + "&date=" + getCountDate());
		if (s != null)
			throw new DuplicateException(warehouse + " stock take on\n" + getCountDate());
	}
}
