package ph.txtdis.dyvek.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dyvek.service.OrderService;
import ph.txtdis.fx.control.AppButtonImpl;

public abstract class AbstractOpenListedOrderApp< //
		OLA extends OpenOrderListApp, //
		LA extends OrderListApp, //
		S extends OrderService> //
		extends AbstractOrderApp<LA, S> {

	@Autowired
	protected OLA openOrderListApp;

	@Autowired
	protected AppButtonImpl openOrderButton;

	@Override
	protected List<AppButtonImpl> addButtons() {
		List<AppButtonImpl> b = super.addButtons();
		b.add(openOrderButton.icon("list").tooltip("List open...").build());
		return b;
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		openOrderButton.onAction(e -> startOpenOrderListApp());
	}

	protected void startOpenOrderListApp() {
		openOrderListApp.addParent(this).start();
		Long id = openOrderListApp.getSelectedKey();
		if (id != null)
			actOn(id.toString(), "");
	}
}
