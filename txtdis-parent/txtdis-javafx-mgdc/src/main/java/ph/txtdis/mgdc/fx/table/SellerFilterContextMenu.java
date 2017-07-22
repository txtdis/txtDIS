package ph.txtdis.mgdc.fx.table;

import static java.util.stream.Collectors.toList;
import static javafx.collections.FXCollections.observableArrayList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import ph.txtdis.dto.SellerSold;
import ph.txtdis.fx.dialog.MessageDialog;
import ph.txtdis.mgdc.service.SellerFilteredService;

@Scope("prototype")
@Component("sellerFilterContextMenu")
public class SellerFilterContextMenu<T extends SellerSold> {

	@Autowired
	private MessageDialog dialog;

	private SellerFilteredService<T> sellerFilteredService;

	private TableView<T> table;

	public void setMenu(SellerFilteredService<T> service, TableView<T> table) {
		try {
			this.sellerFilteredService = service;
			this.table = table;
			table.setContextMenu(menu());
		} catch (Exception e) {
			e.printStackTrace();
			dialog.show(e).addParent(table).start();
		}
	}

	private ContextMenu menu() throws Exception {
		ContextMenu m = new ContextMenu();
		m.getItems().setAll(menuItems());
		return m;
	}

	private MenuItem menuItem(String seller) {
		MenuItem i = new MenuItem(seller);
		i.setOnAction(e -> setTableItems(seller));
		return i;
	}

	private List<MenuItem> menuItems() {
		return sellerFilteredService.listSellers().stream().map(s -> menuItem(s)).collect(toList());
	}

	private void setTableItems(String seller) {
		table.setItems(observableArrayList(sellerFilteredService.list(seller)));
	}
}
