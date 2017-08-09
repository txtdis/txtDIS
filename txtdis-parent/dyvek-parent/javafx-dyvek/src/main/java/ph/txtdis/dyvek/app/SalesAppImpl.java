package ph.txtdis.dyvek.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dyvek.fx.table.OrderTable;
import ph.txtdis.dyvek.service.SalesService;
import ph.txtdis.fx.control.AppFieldImpl;

import java.math.BigDecimal;

import static ph.txtdis.type.Type.TEXT;

@Scope("prototype")
@Component("salesApp")
public class SalesAppImpl
	extends AbstractTabledOpenListedOrderApp<OpenSalesListApp, SearchedSalesListApp, OrderTable, SalesService>
	implements SalesApp {

	@Autowired
	private AppFieldImpl<BigDecimal> toleranceInput;

	@Override
	protected void firstGridLine() {
		comboAndInputGridNodes("Customer", customerCombo.width(310), "P/O No.", orderNoInput.width(110).build(TEXT), 0,
			3);
		dateGridNodes("Date", orderDateDisplay, orderDatePicker, 6, 0, 1);
		percentInputGridNodes("±%Tolerance", toleranceInput, 8, 0);
	}

	@Override
	protected void secondGridLine() {
		super.secondGridLine();
		qtyInKgDisplayGridNodes("Balance", balanceDisplay, 8, 1, 1);
	}

	@Override
	@Lookup("openSalesListApp")
	protected OpenSalesListApp openOrderListApp() {
		return null;
	}

	@Override
	@Lookup("searchedSalesListApp")
	protected SearchedSalesListApp orderListApp() {
		return null;
	}

	@Override
	public void refresh() {
		super.refresh();
		customerCombo.items(service.listCustomers());
		toleranceInput.setValue(service.getTolerancePercent());
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		if (toleranceInput.getValue() != null && service.isNew())
			toleranceInput.onAction(e -> service.setTolerancePercent(toleranceInput.getValue()));
	}
}
