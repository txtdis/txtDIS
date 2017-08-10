package ph.txtdis.mgdc.gsm.app;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.fx.table.BillableTable;
import ph.txtdis.mgdc.gsm.service.RmaService;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.ID;

public abstract class AbstractRmaApp<RS extends RmaService, BT extends BillableTable> //
	extends AbstractBillableApp<RS, BT, Long> {

	protected static final int WIDTH = 120;

	@Autowired
	protected AppButton receiptButton;

	private BooleanProperty returnIsValid;

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> b = new ArrayList<>(super.addButtons());
		b.addAll(asList(invalidateButton, receiptButton));
		return b;
	}

	@Override
	protected void buildButttons() {
		super.buildButttons();
		receiptButton.icon(receiptButtonIconName()).tooltip("Receive...").build();
	}

	protected abstract String receiptButtonIconName();

	@Override
	protected void topGridLine() {
		gridPane.add(label.field("Date"), 0, 0);
		gridPane.add(orderDateStackPane(), 1, 0);
		gridPane.add(label.field("RMA No."), 2, 0);
		gridPane.add(referenceIdInput.width(WIDTH).build(ID), 3, 0);
		gridPane.add(label.field("R/R No."), 4, 0);
		gridPane.add(receivingIdDisplay.readOnly().width(WIDTH).build(ID), 5, 0);
	}

	@Override
	protected void secondGridLine() {
		customerWithoutDueDateGridLine(1, 5);
	}

	@Override
	protected void addressAndOrRemarksGridLine() {
		addressGridNodes(3, 6);
		remarksGridLineAtRowSpanning(4, 6);
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		List<Node> l = new ArrayList<>(super.mainVerticalPaneNodes());
		l.add(trackedPane());
		l.add(receivingPane());
		return l;
	}

	@Override
	protected HBox trackedPane() {
		List<Node> l = new ArrayList<>(super.trackedPane().getChildren());
		l.addAll(decisionNeededApp.addApprovalNodes());
		return pane.centeredHorizontal(l);
	}

	protected HBox receivingPane() {
		return pane.centeredHorizontal(receivingNodes());
	}

	@Override
	protected void renew() {
		super.renew();
		orderDatePicker.requestFocus();
	}

	@Override
	protected void setButtonBindings() {
		super.setButtonBindings();
		saveButton.disableIf(isPosted() //
			.or(table.isEmpty()));
		receiptButton.disableIf(notValidReturn() //
			.or(receivedOnDisplay.isNotEmpty()) //
			.or(decisionNeededApp.isAudited().not()));
		invalidateButton.disableIf(isNew() //
			.or(notValidReturn()));
	}

	protected BooleanBinding notValidReturn() {
		return returnIsValid.not();
	}

	@Override
	protected void setBooleanProperties() {
		returnIsValid = new SimpleBooleanProperty(service.isReturnValid());
	}

	@Override
	protected void setInputFieldBindings() {
		super.setInputFieldBindings();
		referenceIdInput.readOnly();
		customerBox.disableIdInputIf(isPosted());
		customerBox.setSearchButtonVisibleIfNot(isPosted());
	}

	@Override
	public void goToDefaultFocus() {
		newButton.requestFocus();
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		invalidateButton.onAction(e -> invalidateRma());
		receiptButton.onAction(e -> saveReturnReceiptData());
		customerBox.onAction(e -> updateUponCustomerValidation());
	}

	private void invalidateRma() {
		service.invalidate();
		save();
	}

	private void saveReturnReceiptData() {
		try {
			service.saveReturnReceiptData();
		} catch (Information i) {
			showInfoDialog(i);
		} catch (Exception e) {
			showErrorDialog(e);
		} finally {
			refresh();
		}
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

	@Override
	public void refresh() {
		super.refresh();
		returnIsValid.set(service.isReturnValid());
	}

	private void setFocusAfterCustomerValidation() {
		if (customerBox.isEmpty().get())
			customerBox.requestFocus();
		else
			table.requestFocus();
	}

	@Override
	protected void refreshLogNodes() {
		super.refreshLogNodes();
		billedByDisplay.setValue(service.getBilledBy());
		billedOnDisplay.setValue(service.getBilledOn());
	}
}
