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
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import ph.txtdis.dto.Keyed;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.dialog.SearchDialog;
import ph.txtdis.info.Information;
import ph.txtdis.service.MasterService;

public abstract class AbstractMasterApp<AS extends MasterService<T>, T extends Keyed<Long>> //
		extends AbstractKeyedApp<AS, T, Long, Long> {

	@Autowired
	private AppFieldImpl<String> deactivatedByDisplay, lastModifiedByDisplay;

	@Autowired
	private AppFieldImpl<ZonedDateTime> deactivatedOnDisplay, lastModifiedOnDisplay;

	@Autowired
	private SearchDialog searchDialog;

	@Autowired
	protected AppButtonImpl deOrReActivationButton, searchButton;

	@Override
	protected List<AppButtonImpl> addButtons() {
		List<AppButtonImpl> l = new ArrayList<>(super.addButtons());
		l.add(searchButton.icon("search").tooltip("Search...").build());
		l.add(deOrReActivationButton.icon("deactivate").tooltip("Deactivate...").build());
		l.add(decisionButton = decisionNeededApp.addDecisionButton());
		return l;
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(trackedPane());
	}

	@Override
	public void refresh() {
		refreshDeactivationNodes();
		refreshLastModificationNodes();
		super.refresh();
	}

	private void refreshDeactivationNodes() {
		deactivatedByDisplay.setValue(service.getDeactivatedBy());
		deactivatedOnDisplay.setValue(service.getDeactivatedOn());
	}

	private void refreshLastModificationNodes() {
		lastModifiedByDisplay.setValue(service.getLastModifiedBy());
		lastModifiedOnDisplay.setValue(service.getLastModifiedOn());
	}

	@Override
	protected void setBindings() {
		setDecisionButtonBinding();
		setSaveButtonBinding();
		setDeOrActivationButton();
	}

	protected void setDecisionButtonBinding() {
		decisionButton.disableIf(isNew());
	}

	protected abstract void setSaveButtonBinding();

	protected void setDeOrActivationButton() {
		deOrReActivationButton.disableIf(isNew() //
				.or(isAlreadyDeactivated()));
	}

	protected BooleanBinding isAlreadyDeactivated() {
		return deactivatedByDisplay.isNotEmpty();
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		searchButton.onAction(e -> openSearchDialog());
		deOrReActivationButton.onAction(e -> deactivate());
		decisionNeededApp.setDecisionButtonOnAction(e -> showDecisionDialogToValidateOrder());
	}

	private void openSearchDialog() {
		searchDialog.criteria("name").start();
		String name = searchDialog.getText();
		if (name != null)
			search(name);
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

	protected abstract void listSearchResults() throws Exception;

	protected void deactivate() {
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

	private void showDecisionDialogToValidateOrder() {
		decisionNeededApp.showDecisionDialogForValidation(this, service);
		refresh();
	}

	@Override
	protected HBox trackedPane() {
		List<Node> l = new ArrayList<>(super.trackedPane().getChildren());
		l.addAll(deactivationNodes());
		l.addAll(lastModificationNodes());
		return box.forHorizontalPane(l);
	}

	protected List<Node> deactivationNodes() {
		return Arrays.asList(//
				label.name("Deactivated by"), deactivatedByDisplay.readOnly().width(120).build(TEXT), //
				label.name("on"), deactivatedOnDisplay.readOnly().build(TIMESTAMP));
	}

	protected List<Node> lastModificationNodes() {
		return asList(//
				label.name("Last Modified by"), lastModifiedByDisplay.readOnly().width(120).build(TEXT), //
				label.name("on"), lastModifiedOnDisplay.readOnly().build(TIMESTAMP));
	}
}
