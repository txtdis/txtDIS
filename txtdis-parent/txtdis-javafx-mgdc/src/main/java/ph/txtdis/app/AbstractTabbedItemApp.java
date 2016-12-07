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

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.tab.InputTab;
import ph.txtdis.fx.tab.ItemTab;
import ph.txtdis.fx.tab.PricingTab;
import ph.txtdis.info.Information;

public abstract class AbstractTabbedItemApp extends AbstractItemApp {

	@Autowired
	private AppButton deactivateButton;

	@Autowired
	private ItemTab itemTab;

	@Autowired
	private AppField<String> deactivatedByDisplay;

	@Autowired
	private AppField<ZonedDateTime> deactivatedOnDisplay;

	@Autowired
	protected PricingTab pricingTab;

	private BooleanProperty noChangesNeedingApproval;

	protected List<InputTab> inputTabs;

	@Override
	public void refresh() {
		inputTabs.forEach(t -> t.refresh());
		refreshDeactivationNodes();
		super.refresh();
	}

	@Override
	public void save() {
		inputTabs.forEach(t -> t.save());
		super.save();
	}

	@Override
	public void setFocus() {
		itemTab.select();
	}

	protected void checkForChangesNeedingApproval(Tab tab) {
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

	protected List<InputTab> inputTabs() {
		return inputTabs = asList(itemTab.build(), pricingTab.build());
	}

	private BooleanBinding isAlreadyDeactivated() {
		return deactivatedByDisplay.isNotEmpty();
	}

	private void refreshDeactivationNodes() {
		deactivatedByDisplay.setValue(service.getDeactivatedBy());
		deactivatedOnDisplay.setValue(service.getDeactivatedOn());
	}

	private void showDecisionDialogToValidateOrder() {
		decisionNeededApp.showDecisionDialogForValidation(this, service);
		refresh();
	}

	private TabPane tabPane() {
		TabPane tabPane = new TabPane();
		tabPane.setStyle("-fx-tab-min-width: 80;");
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		tabPane.getTabs().addAll(tabs());
		return tabPane;
	}

	private List<Tab> tabs() {
		return inputTabs().stream().map(t -> t.asTab()).collect(toList());
	}

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> l = new ArrayList<>(super.addButtons());
		l.add(deactivateButton.icon("deactivate").tooltip("Next...").build());
		l.add(decisionButton = decisionNeededApp.addDecisionButton());
		return l;
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(tabPane(), trackedPane());
	}

	@Override
	protected void setBindings() {
		super.setBindings();
		noChangesNeedingApproval = new SimpleBooleanProperty(true);
		pricingTab.disableIf(itemTab.hasIncompleteData());
		decisionButton.disableIf(notPosted()//
				.or(noChangesNeedingApproval));
		deactivateButton.disableIf(notPosted()//
				.or(isOffSite)//
				.or(isAlreadyDeactivated()));
		saveButton.disableIf(isAlreadyDeactivated()//
				.or(itemTab.needsPrice().and(pricingTab.hasNoPrices()))//
				.or(itemTab.hasIncompleteData())//
				.or(isOffSite));
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		deactivateButton.setOnAction(e -> deactivate());
		itemTab.setOnSelectionChanged(e -> noChangesNeedingApproval.set(true));
		pricingTab.setOnSelectionChanged(e -> checkForChangesNeedingApproval(pricingTab));
		decisionNeededApp.setDecisionButtonOnAction(e -> showDecisionDialogToValidateOrder());
	}

	@Override
	protected HBox trackedPane() {
		List<Node> l = new ArrayList<>(super.trackedPane().getChildren());
		l.addAll(deactivationNodes());
		return box.forHorizontalPane(l);
	}
}
