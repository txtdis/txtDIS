package ph.txtdis.dyvek.service;

import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dto.Keyed;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.model.BillableDetail;
import ph.txtdis.info.Information;
import ph.txtdis.service.RestClientService;
import ph.txtdis.util.ClientTypeMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.UserType.*;
import static ph.txtdis.util.NumberUtils.zeroIfNull;
import static ph.txtdis.util.UserUtils.isUser;

public abstract class AbstractOrderService<CS extends CustomerService> //
	implements OrderService {

	protected static final String CUSTOMER_NO = "Customer No. ";

	@Autowired
	protected CS customerService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private RestClientService<Billable> restClientService;

	@Autowired
	private ClientTypeMap typeMap;

	private Billable billable;

	private List<Billable> billables;

	public AbstractOrderService() {
		reset();
	}

	@Override
	public void reset() {
		billable = null;
		billables = null;
	}

	@Override
	public void close() throws Information, Exception {
		get().setClosedBy("");
		save();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Billable get() {
		if (billable == null)
			set(new Billable());
		return billable;
	}

	@Override
	public <T extends Keyed<Long>> void set(T t) {
		billable = (Billable) t;
	}

	@Override
	public BigDecimal getBalanceQty() {
		return get().getBalanceQty();
	}

	@Override
	public String getClosedBy() {
		return get().getClosedBy();
	}

	@Override
	public ZonedDateTime getClosedOn() {
		return get().getClosedOn();
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
	public List<BillableDetail> getDetails() {
		return get().getDeliveries();
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
	public String getModuleNo() {
		return getOrderNo();
	}

	@Override
	public String getOpenDialogKeyPrompt() {
		return getAbbreviatedModuleNoPrompt();
	}

	@Override
	public String getOpenDialogPrompt() {
		return "";
	}

	@Override
	public LocalDate getOrderDate() {
		return get().getOrderDate();
	}

	@Override
	public void setOrderDate(LocalDate d) {
		get().setOrderDate(d);
	}

	@Override
	public BigDecimal getPriceValue() {
		return zeroIfNull(get().getPriceValue());
	}

	@Override
	public BigDecimal getQty() {
		return zeroIfNull(get().getTotalQty());
	}

	@Override
	public void setQty(BigDecimal qty) {
		get().setTotalQty(qty);
	}

	@Override
	public String getRecipient() {
		return get().getClient();
	}

	@Override
	public String getRemarks() {
		return get().getRemarks();
	}

	@Override
	public void setRemarks(String text) {
		if (text != null && !text.trim().isEmpty())
			get().setRemarks(text.trim());
	}

	@Override
	@SuppressWarnings("unchecked")
	public RestClientService<Billable> getRestClientService() {
		return restClientService;
	}

	@Override
	public BigDecimal getTotalValue() {
		return get().getTotalValue();
	}

	@Override
	public ClientTypeMap getTypeMap() {
		return typeMap;
	}

	@Override
	public List<String> listItems() {
		return isNew() ? itemService.listNames() : asList(getItem());
	}

	@Override
	public String getItem() {
		return get().getItem();
	}

	@Override
	public void setItem(String name) {
		get().setItem(name);
	}

	@Override
	public List<Billable> listSearched() {
		return billables;
	}

	@Override
	public void openByDoubleClickedTableCellKey(String keyId) throws Exception {
		set(findBillable("/" + keyId));
	}

	protected Billable findBillable(String endPt) throws Exception {
		return billableService().getOne(endPt);
	}

	private RestClientService<Billable> billableService() {
		return restClientService.module(getModuleName());
	}

	@Override
	@SuppressWarnings("unchecked")
	public RestClientService<Billable> getRestClientServiceForLists() {
		return restClientService;
	}

	@Override
	public List<Billable> search(String text) throws Exception {
		return billables = findBillables("/search?orderNo=" + text);
	}

	protected List<Billable> findBillables(String endPt) throws Exception {
		return billableService().getList(endPt);
	}

	@Override
	public void setPriceAndTotalValue(BigDecimal amt) {
		get().setPriceValue(amt);
		get().setTotalValue(getQty().multiply(getPriceValue()));
	}

	protected boolean isCashier() {
		return isUser(CASHIER);
	}

	protected boolean isManager() {
		return isUser(MANAGER);
	}

	protected boolean isStockChecker() {
		return isUser(STOCK_CHECKER);
	}
}
