package ph.txtdis.mgdc.ccbpi.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.info.Information;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.service.RestClientService;
import ph.txtdis.type.OrderConfirmationType;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.math.BigDecimal.ZERO;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static ph.txtdis.type.OrderConfirmationType.MANUAL;
import static ph.txtdis.type.OrderConfirmationType.WAREHOUSE;
import static ph.txtdis.type.PriceType.DEALER;
import static ph.txtdis.util.DateTimeUtils.getServerDate;
import static ph.txtdis.util.DateTimeUtils.toLocalDateFromOrderConfirmationFormat;

public abstract class AbstractOrderConfirmationService //
	extends AbstractCokeBillableService {

	protected static final String CUSTOMER_NO = "Customer No. ";

	@Autowired
	protected CokeCustomerService customerService;

	@Override
	public BillableDetail createDetail() {
		BillableDetail d = super.createDetail();
		d.setPriceValue(priceValue(d));
		return d;
	}

	private BigDecimal priceValue(BillableDetail d) {
		if (isType(MANUAL) || isType(WAREHOUSE))
			return regularPriceValue(d);
		return itemService.getCurrentPriceValue(d.getId(), getDueDate(), DEALER);
	}

	private boolean isType(OrderConfirmationType type) {
		return getOrderType().equalsIgnoreCase(type.toString());
	}

	private BigDecimal regularPriceValue(BillableDetail d) {
		BigDecimal price = itemService.getRegularPriceValue(d.getId(), DEALER);
		return price.subtract(customerDiscountValue(d));
	}

	protected String getOrderType() {
		if (orderType() == null)
			get().setPrefix(OrderConfirmationType.REGULAR.toString());
		return orderType();
	}

	private BigDecimal customerDiscountValue(BillableDetail d) {
		try {
			if (itemService.isNotDiscounted(d.getItemVendorNo()))
				throw new Exception();
			return customerService.getCustomerDiscountValue(getCustomerId(), d.getId(), getDueDate());
		} catch (Exception e) {
			return ZERO;
		}
	}

	private String orderType() {
		return get().getPrefix();
	}

	@Override
	public Long getCustomerId() {
		return get().getCustomerVendorId();
	}

	@Override
	public String getOpenDialogKeyPrompt() {
		return getAbbreviatedModuleNoPrompt();
	}

	@Override
	public String getOpenDialogPrompt() {
		return "Format is: " + getOrderConfirmationPrompt() + "\nfor" //
			+ CUSTOMER_NO + CUSTOMER_ID + "'s 1st order on " + DATE;
	}

	public String getOrderConfirmationPrompt() {
		return CUSTOMER_ID + "-" + ORDER_DATE + "/1";
	}

	@Override
	public LocalDate getOrderDate() {
		if (get().getOrderDate() == null)
			setOrderDate(getServerDate());
		return get().getOrderDate();
	}

	@Override
	public void openByDoubleClickedTableCellKey(String id) throws Exception {
		set(findByOrderNo(id));
	}

	@Override
	@SuppressWarnings("unchecked")
	public Billable findByOrderNo(String key) throws Exception {
		Billable b = findByOrderDateAndOutletIdAndSequenceId(orderDateFromOrderNo(key), outletId(key), sequenceId(key));
		return throwNotFoundExceptionIfNull(b, key);
	}

	protected Billable findByOrderDateAndOutletIdAndSequenceId(LocalDate orderDate, Long outletId, Long sequenceId)
		throws Exception {
		return orderService().getOne("/ocs?date=" + orderDate + "&outletId=" + outletId + "&count=" + sequenceId);
	}

	private LocalDate orderDateFromOrderNo(String key) throws Exception {
		try {
			return toLocalDateFromOrderConfirmationFormat(substringBetween(key, "-", "/"));
		} catch (Exception e) {
			throw new NotFoundException(getAbbreviatedModuleNoPrompt() + key);
		}
	}

	private Long outletId(String key) throws NotFoundException {
		try {
			return Long.valueOf(StringUtils.substringBefore(key, "-"));
		} catch (Exception e) {
			throw new NotFoundException(getAbbreviatedModuleNoPrompt() + key);
		}
	}

	private Long sequenceId(String key) throws NotFoundException {
		try {
			return Long.valueOf(StringUtils.substringAfter(key, "/"));
		} catch (Exception e) {
			throw new NotFoundException(getAbbreviatedModuleNoPrompt() + key);
		}
	}

	protected RestClientService<Billable> orderService() {
		return getRestClientService().module(getModuleName());
	}

	@Override
	public void save() throws Information, Exception {
		if (route() == null || route().isEmpty())
			get().setPrefix(OrderConfirmationType.REGULAR.toString());
		set(save(get()));
		throw new SuccessfulSaveInfo(getSavingInfo());
	}

	@Override
	public String getSavingInfo() {
		return getAbbreviatedModuleNoPrompt() + getModuleNo();
	}

	@Override
	public String getModuleNo() {
		return getOrderNo();
	}

	@Override
	public String getOrderNo() {
		return orderConfirmationNo(get());
	}
}
