package ph.txtdis.app;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.TEXT;
import static ph.txtdis.type.Type.TIMESTAMP;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import ph.txtdis.dto.Customer;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.dialog.SearchDialog;
import ph.txtdis.info.Information;
import ph.txtdis.service.CustomerService;

public abstract class AbstractCustomerApp extends AbstractIdApp<CustomerService, Customer, Long, Long>
		implements CustomerApp {

	@Autowired
	private AppButton searchButton, deactivateButton;

	@Autowired
	private AppField<String> deactivatedByDisplay, lastModifiedByDisplay;

	@Autowired
	private AppField<ZonedDateTime> deactivatedOnDisplay, lastModifiedOnDisplay;

	@Autowired
	private SearchDialog searchDialog;

	@Autowired
	private CustomerListApp customerListApp;

	protected BooleanProperty isOffSite;

	public void listSearchResults() throws Exception {
		customerListApp.addParent(this).start();
		Customer c = customerListApp.getSelection();
		if (c != null)
			service.open(c.getId());
	}

	@Override
	public void refresh() {
		refreshDeactivationNodes();
		refreshLastModificationNodes();
		super.refresh();
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

	protected BooleanBinding isAlreadyDeactivated() {
		return deactivatedByDisplay.isNotEmpty();
	}

	protected List<Node> lastModificationNodes() {
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
		lastModifiedByDisplay.setValue(((Customer) service.get()).getLastModifiedBy());
		lastModifiedOnDisplay.setValue(((Customer) service.get()).getLastModifiedOn());
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

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> l = new ArrayList<>(super.addButtons());
		l.add(searchButton.icon("search").tooltip("Search...").build());
		l.add(deactivateButton.icon("deactivate").tooltip("Deactivate...").build());
		l.add(decisionButton = decisionNeededApp.addDecisionButton());
		return l;
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(trackedPane(), modificationPane());
	}

	@Override
	protected HBox trackedPane() {
		List<Node> l = new ArrayList<>(super.trackedPane().getChildren());
		l.addAll(deactivationNodes());
		return box.forHorizontalPane(l);
	}

	private HBox modificationPane() {
		return box.forHorizontalPane(lastModificationNodes());
	}

	@Override
	protected void setBindings() {
		isOffSite = new SimpleBooleanProperty(service.isOffSite());
		setDecisionAndSaveButtonBindings();
		deactivateButton.disableIf(notPosted()//
				.or(isOffSite)//
				.or(isAlreadyDeactivated()));
	}

	protected void setDecisionAndSaveButtonBindings() {
		decisionButton.disableIf(notPosted());
		saveButton.disableIf(isAlreadyDeactivated()//
				.or(isOffSite));
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		searchButton.setOnAction(e -> openSearchDialog());
		deactivateButton.setOnAction(e -> deactivate());
		decisionNeededApp.setDecisionButtonOnAction(e -> showDecisionDialogToValidateOrder());
	}
}
