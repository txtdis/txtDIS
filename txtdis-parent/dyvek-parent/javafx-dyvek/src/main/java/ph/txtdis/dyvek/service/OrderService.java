package ph.txtdis.dyvek.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import ph.txtdis.dyvek.model.Billable;
import ph.txtdis.dyvek.model.BillableDetail;
import ph.txtdis.info.Information;
import ph.txtdis.service.RemarkedAndSpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService;
import ph.txtdis.service.SearchedByNameService;

public interface OrderService //
		extends ListedAndResetableAndSearchedBillableService, SearchedByNameService<Billable>,
		RemarkedAndSpunAndSavedAndOpenDialogAndTitleAndHeaderAndIconAndModuleNamedAndResettableAndTypeMappedService<Long> {

	void close() throws Information, Exception;

	BigDecimal getBalanceQty();

	String getClosedBy();

	ZonedDateTime getClosedOn();

	String getCustomer();

	List<BillableDetail> getDetails();

	String getItem();

	LocalDate getOrderDate();

	BigDecimal getPriceValue();

	BigDecimal getQty();

	String getRecipient();

	BigDecimal getTotalValue();

	List<String> listCustomers();

	List<String> listItems();

	void setCustomer(String name);

	void setItem(String name);

	void setOrderDate(LocalDate d);

	void setOrderNoUponValidation(String no) throws Exception;

	void setPriceAndTotalValue(BigDecimal amt);

	void setQty(BigDecimal qty);
}
