package ph.txtdis.dyvek.app;

import ph.txtdis.dyvek.service.OrderService;
import ph.txtdis.fx.control.AppButton;

import java.util.List;

public abstract class AbstractOpenListedOrderApp<
	OLA extends OpenOrderListApp,
	LA extends OrderListApp,
	S extends OrderService>
	extends AbstractOrderApp<LA, S> {

	AppButton openOrderButton;

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> b = super.addButtons();
		b.add(openOrderButton());
		return b;
	}

	private AppButton openOrderButton() {
		openOrderButton = button.icon("list").tooltip("List open...").build();
		openOrderButton.onAction(e -> startOpenOrderListApp());
		return openOrderButton;
	}

	private void startOpenOrderListApp() {
		OLA openOrderListApp = openOrderListApp();
		openOrderListApp.addParent(this).start();
		Long id = openOrderListApp.getSelectedKey();
		if (id != null)
			actOnSelection(id.toString());
	}

	protected abstract OLA openOrderListApp();

	protected void actOnSelection(String id) {
		actOn(id, "");
	}
}
