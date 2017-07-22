package ph.txtdis.dyvek.service;

import static java.util.Arrays.asList;
import static ph.txtdis.type.UserType.CASHIER;
import static ph.txtdis.type.UserType.MANAGER;
import static ph.txtdis.type.UserType.STOCK_CHECKER;
import static ph.txtdis.util.NumberUtils.zeroIfNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.Keyed;
import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.model.BillableDetail;
import ph.txtdis.info.Information;
import ph.txtdis.service.CredentialService;
import ph.txtdis.service.ReadOnlyService;
import ph.txtdis.service.SavingService;
import ph.txtdis.service.SpunKeyedService;
import ph.txtdis.util.ClientTypeMap;

public abstract class AbstractOrderService<CS extends CustomerService> //
		implements OrderService {

	protected static final String CUSTOMER_NO = "Customer No. ";

	@Autowired
	private CredentialService credentialService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private ReadOnlyService<Billable> readOnlyService;

	@Autowired
	private SavingService<Billable> savingService;

	@Autowired
	private SpunKeyedService<Billable, Long> spunService;

	@Autowired
	protected CS customerService;

	@Autowired
	private ClientTypeMap typeMap;

	private Billable billable;

	private List<Billable> billables;

	public AbstractOrderService() {
		reset();
	}

	@Override
	public void close() throws Information, Exception {
		get().setClosedBy("");
		save();
	}

	protected Billable findBillable(String endPt) throws Exception {
		return readService().getOne(endPt);
	}

	private ReadOnlyService<Billable> readService() {
		return getListedReadOnlyService().module(getModuleName());
	}

	protected List<Billable> findBillables(String endPt) throws Exception {
		return readService().getList(endPt);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Billable get() {
		if (billable == null)
			set(new Billable());
		return billable;
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
	public String getItem() {
		return get().getItem();
	}

	@Override
	public ReadOnlyService<Billable> getListedReadOnlyService() {
		return getReadOnlyService();
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
	public BigDecimal getPriceValue() {
		return zeroIfNull(get().getPriceValue());
	}

	@Override
	public BigDecimal getQty() {
		return zeroIfNull(get().getTotalQty());
	}

	@Override
	@SuppressWarnings("unchecked")
	public ReadOnlyService<Billable> getReadOnlyService() {
		return readOnlyService;
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
	@SuppressWarnings("unchecked")
	public SavingService<Billable> getSavingService() {
		return savingService;
	}

	@Override
	public SpunKeyedService<Billable, Long> getSpunService() {
		return spunService;
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
	public List<Billable> listSearched() {
		return billables;
	}

	@Override
	public void openByDoubleClickedTableCellKey(String keyId) throws Exception {
		set(findBillable("/" + keyId));
	}

	@Override
	public void reset() {
		billable = null;
		billables = null;
	}

	@Override
	public List<Billable> search(String text) throws Exception {
		return billables = findBillables("/search?orderNo=" + text);
	}

	@Override
	public <T extends Keyed<Long>> void set(T t) {
		billable = (Billable) t;
	}

	@Override
	public void setId(Long id) {
		get().setId(id);
	}

	@Override
	public void setItem(String name) {
		get().setItem(name);
	}

	@Override
	public void setOrderDate(LocalDate d) {
		get().setOrderDate(d);
	}

	@Override
	public void setPriceAndTotalValue(BigDecimal amt) {
		get().setPriceValue(amt);
		get().setTotalValue(getQty().multiply(getPriceValue()));
	}

	@Override
	public void setQty(BigDecimal qty) {
		get().setTotalQty(qty);
	}

	@Override
	public void setRemarks(String text) {
		if (text != null && !text.trim().isEmpty())
			get().setRemarks(text.trim());
	}

	protected boolean isCashier() {
		return credentialService.isUser(CASHIER);
	}

	protected boolean isManager() {
		return credentialService.isUser(MANAGER);
	}

	protected boolean isStockChecker() {
		return credentialService.isUser(STOCK_CHECKER);
	}
}
