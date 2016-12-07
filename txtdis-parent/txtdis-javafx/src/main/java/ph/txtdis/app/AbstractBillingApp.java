package ph.txtdis.app;

import static ph.txtdis.type.Type.CODE;
import static ph.txtdis.type.Type.ID;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.table.BillableTable;
import ph.txtdis.service.BillingService;
import ph.txtdis.type.BillableType;

public class AbstractBillingApp<AS extends BillingService, AT extends BillableTable, PK> //
		extends AbstractBillableApp<AS, AT, PK> implements BillingApp {

	@Autowired
	private AppField<String> idSuffixInput;

	@Autowired
	protected AppField<Long> idNoInput;

	@Autowired
	protected AppField<String> idPrefixInput;

	private BillableType type;

	@Override
	protected void buildDisplays() {
		super.buildDisplays();
		buildIdNoInput();
		idPrefixInput.width(70).build(CODE);
		idSuffixInput.width(40).build(CODE);
	}

	private void buildIdNoInput() {
		if (type == BillableType.INVOICE)
			idNoInput.build(ID);
		else
			idNoInput.width(180).build(ID);
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		List<Node> l = new ArrayList<>(super.mainVerticalPaneNodes());
		l.add(paymentPane());
		l.add(trackedPane());
		return l;
	}

	private Node paymentPane() {
		List<Node> l = new ArrayList<>(vatAndOrTotalNodes());
		l.addAll(paymentNodes());
		return box.forHorizontalPane(l);
	}

	private List<Node> vatAndOrTotalNodes() {
		if (type == BillableType.INVOICE)
			return vatNodes();
		return totalNodes();
	}

	private void refreshVatNodes() {
		vatableDisplay.setValue(service.getVatable());
		vatDisplay.setValue(service.getVat());
	}

	private void refreshPaymentNodes() {
		paymentCombo.itemsSelectingFirst(service.getPayments());
		balanceDisplay.setValue(service.getBalance());
	}

	private void updateUponOrderNoValidation() {
		if (service.isNew())
			try {
				service.updateUponOrderNoValidation( //
						idPrefixInput.getValue(), //
						idNoInput.getValue(), //
						idSuffixInput.getValue());
			} catch (Exception e) {
				idSuffixInput.setValue(null);
				idNoInput.setValue(null);
				handleError(e, idPrefixInput);
			}
	}

	@Override
	protected void addressAndOrRemarksGridLines() {
		addressGridNodes();
		addRemarksAtLine(3, 8);
	}

	@Override
	protected void customerGridLine() {
		customerWithDueDateGridLine();
	}

	@Override
	protected BooleanBinding isPosted() {
		return billedOnDisplay.isNotEmpty();
	}

	@Override
	protected Node orderDateNode() {
		return orderDateDisplayOnly();
	}

	@Override
	public void refresh() {
		refreshSummaryPane();
		idNoInput.setValue(service.getNumId());
		idPrefixInput.setValue(service.getPrefix());
		idSuffixInput.setValue(service.getSuffix());
		super.refresh();
	}

	protected void refreshSummaryPane() {
		totalDisplay.setValue(service.getTotalValue());
		refreshVatNodes();
		refreshPaymentNodes();
	}

	@Override
	protected void setButtonBindings() {
		decisionButton.disableIf(notPosted());
		saveButton.disableIf(isPosted());
	}

	@Override
	protected void setInputFieldBindings() {
		super.setInputFieldBindings();
		idPrefixInput.disableIf(isPosted());//
		idSuffixInput.disableIf(isPosted() //
				.or(idNoInput.isEmpty()));
		setCustomerBoxBindings();
		setIdNoInputBindings();
	}

	private void setIdNoInputBindings() {
		if (type != BillableType.INVOICE) {
			idNoInput.disableProperty().unbind();
			idNoInput.setDisable(true);
		} else
			idNoInput.disableIf(isPosted());

	}

	protected void setCustomerBoxBindings() {
		customerBox.disableIdInput();
		customerBox.hideSearchButton();
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		idSuffixInput.setOnAction(e -> updateUponOrderNoValidation());
		referenceIdInput.setOnAction(e -> updateUponReferenceIdValidation());
		table.setOnItemChange(e -> updateSummaries());
	}

	private void updateUponReferenceIdValidation() {
		Long id = referenceIdInput.getValue();
		if (service.isNew() && id != 0)
			try {
				service.updateUponReferenceIdValidation(id);
			} catch (Exception e) {
				handleError(e, referenceIdInput);
			} finally {
				refresh();
				setFocusAfterReferenceIdValidation();
			}
	}

	protected void setFocusAfterReferenceIdValidation() {
		if (referenceIdInput.isEmpty().get())
			referenceIdInput.requestFocus();
		else
			saveButton.requestFocus();
	}

	protected void updateSummaries() {
		service.updateSummaries(table.getItems());
		refreshSummaryPane();
	}

	@Override
	public void start() {
		service.setType(type);
		super.start();
	}

	@Override
	protected void topGridLine() {
		dateGridNodes();
		idGridNodes();
		referenceGridNodes(service.getReferencePrompt(), 5);
		receivingGridNodes(service.getReceivingPrompt(), 7);
	}

	private void idGridNodes() {
		gridPane.add(label.field(service.getIdPrompt()), 3, 0);
		gridPane.add(idBox(), 4, 0);
	}

	private Node idBox() {
		if (type == BillableType.DELIVERY_REPORT)
			return box.forHorizontals(idNoInput);
		return box.forHorizontals(idPrefixInput, idNoInput, idSuffixInput);

	}

	@Override
	protected HBox trackedPane() {
		List<Node> l = new ArrayList<>(super.trackedPane().getChildren());
		l.addAll(billingNodes(service.getBillingPrompt()));
		l.addAll(receivingNodes());
		return box.forHorizontalPane(l);
	}

	@Override
	public Startable type(BillableType type) {
		this.type = type;
		return this;
	}

	@Override
	public BillableType type() {
		return type;
	}
}
