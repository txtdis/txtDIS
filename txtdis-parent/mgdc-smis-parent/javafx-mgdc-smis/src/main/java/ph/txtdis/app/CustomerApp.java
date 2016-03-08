package ph.txtdis.app;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.type.Type.TEXT;
import static ph.txtdis.type.Type.TIMESTAMP;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import ph.txtdis.dto.Customer;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.dialog.SearchDialog;
import ph.txtdis.fx.tab.CreditTab;
import ph.txtdis.fx.tab.CustomerDiscountTab;
import ph.txtdis.fx.tab.CustomerTab;
import ph.txtdis.fx.tab.InputTab;
import ph.txtdis.info.Information;
import ph.txtdis.service.CustomerService;

@Scope("prototype")
@Component("customerApp")
public class CustomerApp extends AbstractIdApp<CustomerService, Long, Long> {

	@Autowired
	private AppButton searchButton, deactivateButton, uploadButton;

	@Autowired
	private CustomerTab customerTab;

	@Autowired
	private CreditTab creditTab;

	@Autowired
	private CustomerDiscountTab discountTab;

	@Autowired
	private AppField<String> deactivatedByDisplay, lastModifiedByDisplay;

	@Autowired
	private AppField<ZonedDateTime> deactivatedOnDisplay, lastModifiedOnDisplay;

	@Autowired
	private SearchDialog searchDialog;

	@Autowired
	private CustomerListApp customerListApp;
	
	@Autowired
	private SalesforceUploadApp salesforce;

	private List<InputTab> inputTabs;

	private BooleanProperty isOffSite, noChangesNeedingApproval;

	public void listSearchResults() throws Exception {
		customerListApp.addParent(this).start();
		Customer c = customerListApp.getSelection();
		if (c != null)
			service.open(c.getId());
	}

	@Override
	public void refresh() {
		inputTabs.forEach(t -> t.refresh());
		refreshDeactivationNodes();
		refreshLastModificationNodes();
		salesforce.refresh();
		super.refresh();
	}

	@Override
	public void save() {
		inputTabs.forEach(t -> t.save());
		super.save();
	}

	@Override
	public void setFocus() {
		customerTab.select();
	}

	private void checkForChangesNeedingApproval(Tab tab) {
		if (tab.isSelected()) {
			boolean b = service.noChangesNeedingApproval(tab.getText());
			noChangesNeedingApproval.set(b);
		}
	}

	private void deactivate() {
		try {
			service.deactivate();
		} catch (Exception e) {
			showErrorDialog(e);
		} catch (Information i) {
			dialog.show(i).addParent(this).start();
		} finally {
			refresh();
		}
	}

	private List<Node> deactivationNodes() {
		return Arrays.asList(//
				label.name("Deactivated by"), deactivatedByDisplay.readOnly().width(120).build(TEXT), //
				label.name("on"), deactivatedOnDisplay.readOnly().build(TIMESTAMP));
	}

	private List<InputTab> inputTabs() {
		return inputTabs = asList(customerTab.build(), creditTab.build(), discountTab.build());
	}

	private BooleanBinding isAlreadyDeactivated() {
		return deactivatedByDisplay.isNotEmpty();
	}

	private List<Node> lastModificationNodes() {
		return asList(//
				label.name("Last Modified by"), lastModifiedByDisplay.readOnly().width(120).build(TEXT), //
				label.name("on"), lastModifiedOnDisplay.readOnly().build(TIMESTAMP));
	}

	private void openSearchDialog() {
		searchDialog.criteria("name").start();
		String name = searchDialog.getText();
		if (name != null)
			search(name);
	}

	private void refreshDeactivationNodes() {
		deactivatedByDisplay.setValue(service.getDeactivatedBy());
		deactivatedOnDisplay.setValue(service.getDeactivatedOn());
	}

	private void refreshLastModificationNodes() {
		lastModifiedByDisplay.setValue(service.get().getLastModifiedBy());
		lastModifiedOnDisplay.setValue(service.get().getLastModifiedOn());
	}

	private void search(String name) {
		try {
			service.search(name);
			listSearchResults();
			refresh();
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}

	private void showDecisionDialogToValidateOrder() {
		decisionNeededApp.showDecisionDialogForValidation(this, service);
		refresh();
	}

	private List<Tab> tabs() {
		return inputTabs().stream().map(t -> t.asTab()).collect(toList());
	}

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> l = new ArrayList<>(super.addButtons());
		l.add(searchButton.icon("search").tooltip("Search...").build());
		l.add(deactivateButton.icon("deactivate").tooltip("Deactivate...").build());
		l.add(decisionButton = decisionNeededApp.addDecisionButton());
		l.add(uploadButton = salesforce.service(service).stage(this).addUploadButton());
		return l;
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(tabPane(), trackedPane(), modificationAndUploadPane());
	}

	private TabPane tabPane() {
		TabPane tabPane = new TabPane();
		tabPane.setStyle("-fx-tab-min-width: 80;");
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		tabPane.getTabs().setAll(tabs());
		return tabPane;
	}

	@Override
	protected HBox trackedPane() {
		List<Node> l = new ArrayList<>(super.trackedPane().getChildren());
		l.addAll(deactivationNodes());
		return box.forHorizontalPane(l);
	}

	private HBox modificationAndUploadPane() {
		List<Node> l = new ArrayList<>(lastModificationNodes());
		l.addAll(salesforce.addNodes());
		return box.forHorizontalPane(l);
	}

	@Override
	protected void setBindings() {
		isOffSite = new SimpleBooleanProperty(service.isOffSite());
		noChangesNeedingApproval = new SimpleBooleanProperty(true);
		customerTab.disableNameFieldIf(createdOnDisplay.isNotEmpty());
		creditTab.disableIf(customerTab.showsPartnerAsACustomer().not());
		discountTab.disableIf(creditTab.disabledProperty());
		decisionButton.disableIf(notPosted()//
				.or(noChangesNeedingApproval));
		deactivateButton.disableIf(notPosted()//
				.or(isOffSite)//
				.or(isAlreadyDeactivated()));
		saveButton.disableIf(isAlreadyDeactivated()//
				.or(customerTab.hasIncompleteData())//
				.or(isOffSite));
		uploadButton.disableIf(isOffSite);
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		searchButton.setOnAction(e -> openSearchDialog());
		deactivateButton.setOnAction(e -> deactivate());
		customerTab.setOnSelectionChanged(e -> noChangesNeedingApproval.set(true));
		creditTab.setOnSelectionChanged(e -> checkForChangesNeedingApproval(creditTab));
		discountTab.setOnSelectionChanged(e -> checkForChangesNeedingApproval(discountTab));
		decisionNeededApp.setDecisionButtonOnAction(e -> showDecisionDialogToValidateOrder());
	}
}
