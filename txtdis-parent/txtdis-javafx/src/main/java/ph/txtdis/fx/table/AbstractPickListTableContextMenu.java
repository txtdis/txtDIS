package ph.txtdis.fx.table;

import static java.util.stream.Collectors.toList;
import static javafx.beans.binding.Bindings.when;
import static javafx.collections.FXCollections.observableArrayList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import ph.txtdis.dto.Booking;
import ph.txtdis.fx.dialog.MessageDialog;
import ph.txtdis.service.PickListService;

public abstract class AbstractPickListTableContextMenu implements PickListTableContextMenu {

	@Autowired
	private MessageDialog dialog;

	@Autowired
	protected PickListService service;

	private TableView<Booking> table;

	private void addBookingMenuItemsToDeleteMenu(TableView<Booking> t, ContextMenu m) {
		t.getContextMenu().getItems().forEach(b -> {
			MenuItem i = new MenuItem(b.getText());
			i.setGraphic(b.getGraphic());
			i.setOnAction(b.getOnAction());
			m.getItems().add(i);
		});
	}

	private void addBookings(String route) {
		ObservableList<Booking> bookings = observableArrayList(table.getItems());
		bookings = addBookings(bookings, route);
		table.setItems(bookings);
	}

	protected abstract ObservableList<Booking> addBookings(ObservableList<Booking> bookings, String route);

	private void append(String route) {
		addBookings(route);
		service.setBookings(table.getItems());
		refreshTable();
	}

	private void delete(TableRow<Booking> r) {
		table.getItems().remove(r.getItem());
		service.unpick(r.getItem());
		service.setBookings(table.getItems());
		refreshTable();
	}

	private MenuItem deleteMenuItem(TableRow<Booking> r) {
		MenuItem i = new MenuItem("Remove");
		i.setOnAction(e -> delete(r));
		return i;
	}

	private ContextMenu menu() throws Exception {
		ContextMenu m = new ContextMenu();
		m.getItems().setAll(menuItems());
		return m;
	}

	private MenuItem menuItem(String route) {
		MenuItem i = new MenuItem(route);
		i.setOnAction(e -> append(route));
		return i;
	}

	private List<MenuItem> menuItems() throws Exception {
		return service.listRoutes().stream().map(r -> menuItem(r)).collect(toList());
	}

	private void refreshTable() {
		setMenu(table);
		table.refresh();
		table.scrollTo(table.getItems().size() - 1);
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
}
