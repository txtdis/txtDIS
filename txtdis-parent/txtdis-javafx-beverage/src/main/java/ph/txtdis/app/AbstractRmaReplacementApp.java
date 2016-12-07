package ph.txtdis.app;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.DATE;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;
import static ph.txtdis.type.Type.TIMESTAMP;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.table.BillableTable;
import ph.txtdis.service.RmaReplacementService;

public class AbstractRmaReplacementApp<AS extends RmaReplacementService, AT extends BillableTable>
		extends AbstractBillableApp<AS, AT, Long> {

	@Autowired
	private AppButton receiptButton;

	@Autowired
	private AppField<Long> replacementIdDisplay;

	@Autowired
	private AppField<String> replacedByDisplay;

	@Autowired
	private AppField<ZonedDateTime> replacedOnDisplay;

	private BooleanProperty isOffSite, returnIsValid;

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> b = new ArrayList<>(super.addButtons());
		b.addAll(asList(decisionButton, invalidateButton, receiptButton));
		return b;
	}

	@Override
	protected void addressAndOrRemarksGridLines() {
		addressGridNodes();
		addRemarksAtLine(3, 8);
	}

	@Override
	protected void buildButttons() {
		super.buildButttons();
		receiptButton.icon("returnReceipt").tooltip("Receive...").build();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		List<Node> l = new ArrayList<>(super.mainVerticalPaneNodes());
		l.add(trackedPane());
		l.add(returnedItemPane());
		return l;
	}

	@Override
	protected void customerGridLine() {
		gridPane.add(label.field("Customer"), 0, 1);
		gridPane.add(customerBox.build(this, service), 1, 1, 8, 1);
	}

	private Node returnedItemPane() {
		return box.forHorizontalPane(asList( //
				label.name("Received by"), receivedByDisplay.readOnly().width(120).build(TEXT), //
				label.name("on"), receivedOnDisplay.readOnly().build(TIMESTAMP), //
				label.name("Replaced by"), replacedByDisplay.readOnly().width(120).build(TEXT), //
				label.name("on"), replacedOnDisplay.readOnly().build(TIMESTAMP)));
	}

	@Override
	public void refresh() {
		super.refresh();
		returnIsValid.set(service.isReturnValid());
		replacementIdDisplay.setValue(service.getReplacementId());
	}

	@Override
	protected void reset() {
		super.reset();
		customerBox.requestFocus();
	}

	@Override
	protected void setBindings() {
		isOffSite = new SimpleBooleanProperty(service.isOffSite());
		returnIsValid = new SimpleBooleanProperty(service.isReturnValid());
		customerBox.disableIdInputIf(isPosted());
		customerBox.setSearchButtonVisibleIfNot(isPosted());
		table.disableIf(customerBox.isNameDisplayEmpty());
	}

	private BooleanBinding notValidReturn() {
		return returnIsValid.not();
	}

	@Override
	public void setFocus() {
		newButton.requestFocus();
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		customerBox.setOnAction(e -> updateUponCustomerValidation());
		decisionNeededApp.setDecisionButtonOnAction(e -> showAuditDialogToValidateOrder());
		invalidateButton.setOnAction(e -> invalidateThis());
		receiptButton.setOnAction(e -> saveItemReturnReceiptData());
	}

	private void updateUponCustomerValidation() {
		Long id = customerBox.getId();
		if (service.isNew() && id != 0)
			try {
				service.updateUponCustomerIdValidation(id);
			} catch (Exception e) {
				customerBox.handleError(e);
			} finally {
				refresh();
				setFocusAfterCustomerValidation();
			}
	}

	private void setFocusAfterCustomerValidation() {
		if (customerBox.isNameDisplayEmpty().get())
			customerBox.requestFocus();
		else
			table.requestFocus();
	}

	private void showAuditDialogToValidateOrder() {
		decisionNeededApp.showDecisionDialogForValidation(this, service);
		refresh();
	}

	private void invalidateThis() {
		service.invalidate();
		save();
	}

	private void saveItemReturnReceiptData() {
		try {
			service.saveItemReturnReceiptData();
			save();
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}

	@Override
	protected void topGridLine() {
		gridPane.add(label.field("Date"), 0, 0);
		gridPane.add(orderDateDisplay.readOnly().build(DATE), 1, 0);
		gridPane.add(label.field("RMA No."), 2, 0);
		gridPane.add(referenceIdInput.build(ID), 3, 0);
		gridPane.add(label.field("R/R No."), 4, 0);
		gridPane.add(receivingIdDisplay.readOnly().build(ID), 5, 0);
		gridPane.add(label.field("P/L No."), 6, 0);
		gridPane.add(replacementIdDisplay.readOnly().build(ID), 7, 0);
	}

	@Override
	protected HBox trackedPane() {
		List<Node> l = new ArrayList<>(super.trackedPane().getChildren());
		l.addAll(decisionNeededApp.addApprovalNodes());
		return box.forHorizontalPane(l);
	}

	@Override
	protected void updateLogNodes() {
		super.updateLogNodes();
		replacedByDisplay.setValue(service.getReplacedBy());
		replacedOnDisplay.setValue(service.getReplacedOn());
	}

	@Override
	protected void setButtonBindings() {
		invalidateButton.disableIf(notValidReturn() //
				.or(isOffSite) //
				.or(replacedOnDisplay.isNotEmpty()));
		receiptButton.disableIf(notValidReturn() //
				.or(receivedOnDisplay.isNotEmpty()));
		saveButton.disableIf(isPosted());
	}

	@Override
	protected List<Node> vatNodes() {
		return super.vatNodes();
	}
}
