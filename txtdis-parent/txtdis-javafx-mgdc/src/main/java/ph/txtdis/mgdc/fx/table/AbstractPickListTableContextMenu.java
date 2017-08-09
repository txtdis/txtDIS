package ph.txtdis.mgdc.fx.table;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dto.Booking;
import ph.txtdis.fx.dialog.MessageDialog;
import ph.txtdis.mgdc.service.PickListService;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static javafx.beans.binding.Bindings.when;
import static javafx.collections.FXCollections.observableArrayList;
import static org.apache.log4j.Logger.getLogger;

public abstract class AbstractPickListTableContextMenu //
	implements PickListTableContextMenu {

	private static Logger logger = getLogger(AbstractPickListTableContextMenu.class);

	@Autowired
	protected PickListService service;

	protected TableView<Booking> table;

	@Autowired
	private MessageDialog dialog;

	protected abstract ObservableList<Booking> addBookings(ObservableList<Booking> bookings, String route);

	@Override
	public void setMenu(TableView<Booking> table) {
		try {
			this.table = table;
			table.setContextMenu(menu());
			table.setRowFactory(t -> row(t));
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

	private List<MenuItem> menuItems() throws Exception {
		List<String> routes = service.listRouteNames();
		logger.info("\n    Routes@menuItems = " + routes);
		return routes.stream().map(r -> menuItem(r)).collect(toList());
	}

	private MenuItem menuItem(String route) {
		MenuItem i = new MenuItem(route);
		i.setOnAction(e -> append(route));
		return i;
	}

	protected void append(String route) {
		addBookings(route);
		service.setBookings(table.getItems());
		refreshTable();
	}

	private void addBookings(String route) {
		ObservableList<Booking> bookings = observableArrayList(table.getItems());
		bookings = addBookings(bookings, route);
		table.setItems(bookings);
	}

	private TableRow<Booking> row(TableView<Booking> t) {
		TableRow<Booking> r = new TableRow<>();
		r.contextMenuProperty()
			.bind(when(r.itemProperty().isNotNull())//
				.then(rowMenu(t, r))//
				.otherwise((ContextMenu) null));
		return r;
	}

	private ContextMenu rowMenu(TableView<Booking> t, TableRow<Booking> r) {
		ContextMenu m = new ContextMenu();
		addBookingMenuItemsToDeleteMenu(t, m);
		m.getItems().add(deleteMenuItem(r));
		return m;
	}

	private void addBookingMenuItemsToDeleteMenu(TableView<Booking> t, ContextMenu m) {
		t.getContextMenu().getItems().forEach(b -> {
			MenuItem i = new MenuItem(b.getText());
			i.setGraphic(b.getGraphic());
			i.setOnAction(b.getOnAction());
			m.getItems().add(i);
		});
	}

	private MenuItem deleteMenuItem(TableRow<Booking> r) {
		MenuItem i = new MenuItem("Remove");
		i.setOnAction(e -> delete(r));
		return i;
	}

	private void delete(TableRow<Booking> r) {
		table.getItems().remove(r.getItem());
		service.unpick(r.getItem());
		service.setBookings(table.getItems());
		refreshTable();
	}

	private void refreshTable() {
		setMenu(table);
		table.refresh();
		table.scrollTo(table.getItems().size() - 1);
	}
}
