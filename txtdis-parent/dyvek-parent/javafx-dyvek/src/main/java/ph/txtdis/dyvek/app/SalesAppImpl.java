package ph.txtdis.dyvek.app;

import static ph.txtdis.type.Type.TEXT;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dyvek.fx.table.OrderTable;
import ph.txtdis.dyvek.service.SalesService;
import ph.txtdis.fx.control.AppFieldImpl;

@Scope("prototype")
@Component("salesApp")
public class SalesAppImpl //
		extends AbstractTabledOpenListedOrderApp<OpenSalesListApp, SearchedSalesListApp, OrderTable, SalesService> //
		implements SalesApp {

	@Autowired
	private AppFieldImpl<BigDecimal> toleranceInput;

	@Override
	protected void firstGridLine() {
		comboAndInputGridNodes("Customer", customerCombo.width(310), "P/O No.", orderNoInput.width(110).build(TEXT), 0, 3);
		dateGridNodes("Date", orderDateDisplay, orderDatePicker, 6, 0, 1);
		percentInputGridNodes("±%Tolerance", toleranceInput, 8, 0);
	}

	@Override
	protected void secondGridLine() {
		super.secondGridLine();
		qtyInKgDisplayGridNodes("Balance", balanceDisplay, 8, 1, 1);
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
		setToleranceListener();
	}

	private void setToleranceListener() {
		if (toleranceInput != null && service.isNew())
			toleranceInput.onAction(e -> service.setTolerancePercent(toleranceInput.getValue()));
	}
}
