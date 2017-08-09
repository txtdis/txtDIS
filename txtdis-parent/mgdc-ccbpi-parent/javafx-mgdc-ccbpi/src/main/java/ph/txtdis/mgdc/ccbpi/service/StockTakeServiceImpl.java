package ph.txtdis.mgdc.ccbpi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.Keyed;
import ph.txtdis.dto.StockTake;
import ph.txtdis.dto.StockTakeDetail;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.mgdc.ccbpi.dto.Item;
import ph.txtdis.mgdc.service.HolidayService;
import ph.txtdis.service.RestClientService;
import ph.txtdis.service.RestClientService;
import ph.txtdis.service.SyncService;
import ph.txtdis.service.UserService;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;
import ph.txtdis.util.ClientTypeMap;
import ph.txtdis.util.DateTimeUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static ph.txtdis.type.UserType.*;
import static ph.txtdis.util.DateTimeUtils.getServerDate;
import static ph.txtdis.util.UserUtils.isUser;
import static ph.txtdis.util.UserUtils.username;

@Service("stockTakeService")
public class StockTakeServiceImpl //
	implements StockTakeService {

	@Autowired
	private HolidayService holidayService;

	@Autowired
	private BommedDiscountedPricedValidatedItemService itemService;

	@Autowired
	private RestClientService<StockTake> restClientService;

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
		return restClientService.module(getModuleName()).getOne("/date?on=" + date);
	}

	@Override
	public String getModuleName() {
		return "stockTake";
	}

	@Override
	public StockTake getByBooking(Long id) throws Exception {
		return restClientService.module(getModuleName()).getOne("/booking?id=" + id);
	}

	@Override
	public String getCreatedBy() {
		return get().getCreatedBy();
	}

	@Override
	@SuppressWarnings("unchecked")
	public StockTake get() {
		if (stockTake == null)
			set(new StockTake());
		return stockTake;
	}

	@Override
	public <T extends Keyed<Long>> void set(T t) {
		stockTake = (StockTake) t;
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
	public void setDetails(List<StockTakeDetail> details) {
		get().setDetails(details);
	}

	@Override
	public String getHeaderName() {
		return getAlternateName();
	}

	@Override
	public String getAlternateName() {
		return "Stock Take";
	}

	@Override
	public Long getId() {
		return get().getId();
	}

	@Override
	public void setId(Long id) {
		get().setId(id);
	}

	@Override
	public Item getItem() {
		return item;
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
	public StockTake getPreviousCount() {
		try {
			return restClientService.module(getModuleName())
				.getOne("/count?warehouse=all&date=" + holidayService.previousWorkDay(getLatestCountDate()));
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
	public StockTake getLatestCount() {
		try {
			return restClientService.module(getModuleName()).getOne("/count?warehouse=all&date=" + getServerDate
				());
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getRemarks() {
		return get().getRemarks();
	}

	@Override
	public void setRemarks(String text) {
		get().setRemarks(text);
	}

	@Override
	@SuppressWarnings("unchecked")
	public RestClientService<StockTake> getRestClientService() {
		return restClientService;
	}

	@Override
	public String getTitleName() {
		return username() + "@" + modulePrefix + " " + StockTakeService.super.getTitleName();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public boolean isUserAStockTaker() {
		return isUser(STOCK_TAKER) || isUser(MANAGER);
	}

	@Override
	public boolean isCountToday() {
		return getCountDate().equals(getServerDate());
	}

	@Override
	public LocalDate getCountDate() {
		if (get().getCountDate() == null)
			get().setCountDate(getServerDate());
		return get().getCountDate();
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
		return s == null ? null : singletonList(s);
	}

	@Override
	public void setChecker(String checkerName) {
		get().setChecker(checkerName);
	}

	@Override
	public void setItemUponValidation(long id) throws Exception {
		item = null;
		item = confirmItemExistsAndIsNotDeactivated(id);
	}

	@Override
	public Item confirmItemExistsAndIsNotDeactivated(Long id) throws Exception {
		return getItemService().findByVendorNo(id.toString());
	}

	@Override
	public BommedDiscountedPricedValidatedItemService getItemService() {
		return itemService;
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
		StockTake t = (StockTake) previous(0L);
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
		if (!isNew() || getCountDate() == null)
			return;
		verifyNoStockTakeMadeOnWarehouse(warehouse);
		verifyAllCountDateTransactionsAreComplete();
		get().setWarehouse(warehouse);
	}

	protected void verifyAllCountDateTransactionsAreComplete() throws Exception {
		// verifyAllPickedLoadOrdersHaveNoItemQuantityVariances("ALL", getCountDate());
		// verifyAllPickedSalesOrderHaveBeenBilled("ALL", getCountDate());
	}

	private void verifyNoStockTakeMadeOnWarehouse(String warehouse) throws Exception {
		StockTake s = restClientService.module(getModuleName())
			.getOne("/count?warehouse=" + warehouse + "&date=" + getCountDate());
		if (s != null)
			throw new DuplicateException(warehouse + " stock take on\n" + getCountDate());
	}
}
