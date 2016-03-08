package ph.txtdis.app;

import static java.util.Arrays.asList;
import static javafx.beans.binding.Bindings.equal;
import static javafx.beans.binding.Bindings.when;
import static javafx.collections.FXCollections.observableArrayList;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;
import static org.apache.commons.lang3.text.WordUtils.uncapitalize;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.DATE;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.QUANTITY;
import static ph.txtdis.type.Type.TEXT;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static ph.txtdis.type.ModuleType.SALES_ORDER;
import static ph.txtdis.type.ModuleType.SALES_RETURN;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ph.txtdis.dto.Billing;
import ph.txtdis.dto.ItemList;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.control.InputControl;
import ph.txtdis.fx.dialog.CustomerDialog;
import ph.txtdis.fx.dialog.SearchDialog;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.fx.table.ItemListTable;
import ph.txtdis.info.SuccessfulSaveInfo;
import ph.txtdis.service.BillingService;
import ph.txtdis.service.CustomerService;
import ph.txtdis.type.ModuleType;

@Scope("prototype")
@Component("salesApp")
public class SalesApp extends AbstractIdApp<BillingService, Long, String> implements MultiTyped {

	private static final String PROMPT = "Select date whose first entry will opened";

	private static Logger logger = getLogger(SalesApp.class);

	@Autowired
	private AppButton customerSearchButton, openByDateButton;

	@Autowired
	private AppCombo<String> collectorCombo;

	@Autowired
	private AppField<LocalDate> orderDateDisplay;

	@Autowired
	private AppField<Long> bookingIdDisplay, customerIdInput;

	@Autowired
	private AppField<String> customerNameDisplay;

	@Autowired
	private AppField<BigDecimal> totalAmountDisplay, totalQuantityDisplay;

	@Autowired
	private CustomerApp customerApp;

	@Autowired
	private CustomerDialog customerDialog;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ItemListTable table;

	@Autowired
	private SearchDialog searchDialog;

	private ObjectProperty<ModuleType> type;

	public void addItemToTable(ItemList entity) {
		ObservableList<ItemList> l = observableArrayList(table.getItems());
		l.add(entity);
		table.setItems(l);
	}

	public void displayCustomerListForSelection() {
		customerApp.addParent(this).start();
	}

	public boolean isASalesOrder() {
		return isSalesOrder().get();
	}

	@Override
	public void refresh() {
		super.refresh();
		table.items(service.getDetails());
		updateTotalDisplays();
		updateCustomerFields();
		orderDateDisplay.setValue(getOrderDate());
		bookingIdDisplay.setValue(billing().getId());
		collectorCombo.items(service.listCollectors());
		if (isNew())
			customerIdInput.requestFocus();
	}

	@Override
	public void save() {
		try {
			billing().setCollector(collectorCombo.getValue());
			service.save();
		} catch (SuccessfulSaveInfo i) {
			dialog.show(i).addParent(this).start();
		} catch (Exception e) {
			showErrorDialog(e);
		} finally {
			refresh();
		}
	}

	public void searchCustomer() {
		String name = openCustomerSearchDialog();
		if (name != null)
			searchCustomer(name);
	}

	@Override
	public void setFocus() {
		newButton.requestFocus();
	}

	@Override
	public void start() {
		service.setType(type.get());
		super.start();
	}

	@Override
	public String type() {
		String s = type.get().toString();
		s = capitalizeFully(s, '_').replace("_", "");
		return uncapitalize(s);
	}

	@Override
	public Startable type(ModuleType t) {
		type = new SimpleObjectProperty<>(t);
		return this;
	}

	private void addNewCustomer() {
		customerDialog.addParent(this).start();
		service.setCustomer(customerDialog.getAddedItem());
		customerDialog.close();
		dialog.close();
		logger.info("Custmer @ add customer = " + service.getCustomer());
		updateCustomerFields();
	}

	private Billing billing() {
		return service.get();
	}

	private void buildDisplays() {
		totalAmountDisplay.readOnly().build(CURRENCY);
	}

	private HBox customerBox() {
		return new HBox(//
				customerIdInput.build(ID), //
				customerNameDisplay.readOnly().width(420).build(TEXT), //
				customerSearchButton.fontSize(16).icon("search").build() //
		);
	}

	private LocalDate getOrderDate() {
		return billing().getOrderDate();
	}

	private AppGridPane gridPane() {
		gridPane.getChildren().clear();
		gridPane.add(label.field("Customer"), 0, 0);
		gridPane.add(customerBox(), 1, 0, 5, 1);
		gridPane.add(label.field("Date"), 0, 1);
		gridPane.add(orderDateDisplay.readOnly().build(DATE), 1, 1);
		gridPane.add(label.field("S/O No."), 2, 1);
		gridPane.add(bookingIdDisplay.readOnly().build(ID), 3, 1);
		gridPane.add(label.field("Collector"), 4, 1);
		gridPane.add(collectorCombo, 5, 1);
		return gridPane;
	}

	private void handleError(Exception e, InputControl<?> control) {
		e.printStackTrace();
		dialog.show(e).addParent(this).start();
		control.setValue(null);
		((Node) control).requestFocus();
	}

	private boolean isAReceivingReport() {
		return isReceivingReport().get();
	}

	private BooleanBinding isReceivingReport() {
		return equal(SALES_RETURN, type);
	}

	private BooleanBinding isSalesOrder() {
		return equal(SALES_ORDER, type);
	}

	private void openByOrderNo(String id) throws Exception {
		Billing i = service.findById(id);
		service.set(i);
		refresh();
	}

	private String openCustomerSearchDialog() {
		customerIdInput.setValue(null);
		searchDialog.criteria("name").addParent(this).start();
		return searchDialog.getText();
	}

	private void searchCustomer(String name) {
		try {
			customerService.search(name);
			displayCustomerListForSelection();
			updateCustomerDisplaysPerSelection();
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}

	private void setFocusAfterBookingIdValidation() {
		if (isAReceivingReport())
			table.requestFocus();
	}

	private void setNew() {
		isNew = lastModifiedOnDisplay.isEmpty().get();
	}

	private void showOpenByDateDialog() {
		String h = service.getOpenDialogHeading();
		openByDateDialog.header(h).prompt(PROMPT).addParent(this).start();
		LocalDate d = openByDateDialog.getDate();
		if (d != null)
			open(d);
	}

	private void showOptionToAddNewCustomer(NotFoundException e) {
		dialog.showOption(e.getMessage() + "\nadd new?", "Add", "Close");
		dialog.setOnAction(a -> addNewCustomer());
		dialog.addParent(this).start();
	}

	private VBox tablePane() {
		VBox v = box.forVerticals(table.build(), totalPane());
		HBox h = box.forHorizontalPane(v);
		return box.forVerticals(h);
	}

	private Node totalPane() {
		return box.forTableTotals(asList(//
				totalQuantityDisplay.readOnly().build(QUANTITY), //
				totalAmountDisplay.readOnly().build(CURRENCY)));
	}

	private void tryOpeningByOrderNo(String id) {
		try {
			openByOrderNo(id);
		} catch (Exception e) {
			e.printStackTrace();
			dialog.show(e).addParent(this).start();
		}
	}

	private void updateCustomerDisplaysPerSelection() {
		service.setCustomer(customerApp.getSelection());
		updateCustomerFields();
	}

	private void updateCustomerFields() {
		customerIdInput.setValue(service.getCustomerId());
		customerNameDisplay.setValue(service.getCustomerName());
	}

	private void updateSummaries() {
		service.updateSummaries(table.getItems());
		updateTotalDisplays();
	}

	private void updateTotalDisplays() {
		totalAmountDisplay.setValue(billing().getTotalValue());
		totalQuantityDisplay.setValue(billing().getTotalQty());
	}

	private void updateUponBookingIdValidation() {
		try {
			service.updateUponBookingIdValidation(bookingIdDisplay.getValue());
		} catch (Exception e) {
			handleError(e, bookingIdDisplay);
		} finally {
			refresh();
			setFocusAfterBookingIdValidation();
		}
	}

	private void updateUponCustomerValidation() {
		try {
			service.updateUponCustomerIdValidation(customerIdInput.getValue());
			updateCustomerFields();
			table.requestFocus();
		} catch (NotFoundException e) {
			showOptionToAddNewCustomer(e);
			updateCustomerFields();
			table.requestFocus();
		} catch (Exception e) {
			handleError(e, customerIdInput);
		}
	}

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> b = new ArrayList<>(super.addButtons());
		b.add(2, openByDateButton.icon("openByDate").tooltip("Open a date's\nfirst entry").build());
		return b;
	}

	@Override
	protected String getTitleText() {
		return isNew() ? newModule() : service.getModuleId() + " " + service.getOrderNo();
	}

	@Override
	protected boolean isNew() {
		if (isNew == null)
			setNew();
		return isNew;
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		buildDisplays();
		return asList(gridPane(), tablePane(), trackedPane());
	}

	@Override
	protected void openSelected() {
		String id = getDialogInput();
		if (id != null && !id.isEmpty())
			tryOpeningByOrderNo(id);
	}

	@Override
	protected void setBindings() {
		bookingIdDisplay.disableIf(isSalesOrder());
		collectorCombo.disableIf(customerIdInput.isEmpty());
		customerIdInput.disableIf(isPosted());
		customerSearchButton.visibleProperty().bind(customerIdInput.isEmpty());
		saveButton.disableIf(
				when(isSalesOrder()).then(totalAmountDisplay.isEmpty().or(isPosted().and(collectorCombo.isEmpty())))//
						.otherwise(isPosted()));
		table.disableIf(collectorCombo.isEmpty());
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		bookingIdDisplay.setOnAction(e -> updateUponBookingIdValidation());
		customerIdInput.setOnAction(e -> updateUponCustomerValidation());
		customerSearchButton.setOnAction(e -> searchCustomer());
		openByDateButton.setOnAction(e -> showOpenByDateDialog());
		table.setOnItemChange(i -> updateSummaries());
	}
}
