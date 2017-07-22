package ph.txtdis.mgdc.gsm.app;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;
import static ph.txtdis.type.Type.TIMESTAMP;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import ph.txtdis.dto.CreditNote;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.mgdc.app.AbstractDecisionNeededApp;
import ph.txtdis.mgdc.fx.table.CreditNoteDataDumpTable;
import ph.txtdis.mgdc.fx.table.CreditNotePaymentTable;
import ph.txtdis.mgdc.service.CreditNoteService;

@Scope("prototype")
@Component("creditNoteApp")
public class CreditNoteAppImpl //
		extends AbstractDecisionNeededApp<CreditNoteService, CreditNote, Long, Long> {

	@Autowired
	private AppButtonImpl unpaidListButton, unvalidatedListButton, dataDumpButton;

	@Autowired
	private AppFieldImpl<BigDecimal> balanceDisplay, totalPaymentDisplay, valueField;

	@Autowired
	private AppFieldImpl<Long> idDisplay;

	@Autowired
	private AppFieldImpl<String> descriptionField, lastModifiedByDisplay, referenceField;

	@Autowired
	private AppFieldImpl<ZonedDateTime> lastModifiedOnDisplay;

	@Autowired
	private CreditNoteDataDumpTable dataDumpTable;

	@Autowired
	private CreditNotePaymentTable paymentTable;

	@Autowired
	private UnpaidCreditNoteListApp unpaidListApp;

	@Autowired
	private UnvalidatedCreditNoteListApp unvalidatedListApp;

	@Override
	protected List<AppButtonImpl> addButtons() {
		List<AppButtonImpl> b = new ArrayList<>(super.addButtons());
		b.add(unvalidatedListButton.icon("list").tooltip("Unvalidated...").build());
		b.add(unpaidListButton.icon("credit").tooltip("Unpaid...").build());
		b.add(dataDumpButton.icon("dataDump").tooltip("Datadump...").build());
		return b;
	}

	@Override
	protected void clear() {
		super.clear();
		paymentTable.removeListener();
	}

	@Override
	public void goToDefaultFocus() {
		if (service.isNew())
			orderDatePicker.requestFocus();
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(gridPane(), tablePane(), paymentPane(), trackedPane(), decisionPane());
	}

	private AppGridPane gridPane() {
		buildFields();
		gridPane.getChildren().clear();
		gridPane.add(label.field("Date"), 0, 0);
		gridPane.add(orderDateStackPane(), 1, 0);
		gridPane.add(label.field("Amount"), 2, 0);
		gridPane.add(valueField.build(CURRENCY), 3, 0);
		gridPane.add(label.field("ID No."), 4, 0);
		gridPane.add(idDisplay.readOnly().build(ID), 5, 0);
		gridPane.add(label.field("Reference No."), 0, 1);
		gridPane.add(referenceField.build(TEXT), 1, 1, 5, 1);
		gridPane.add(label.field("Description"), 0, 2);
		gridPane.add(descriptionField.build(TEXT), 1, 2, 5, 1);
		remarksGridLineAtRowSpanning(3, 5);
		return gridPane;
	}

	private HBox tablePane() {
		return box.forHorizontalPane(paymentTable.build());
	}

	private Node paymentPane() {
		return box.forHorizontalPane(asList(//
				label.name("Balance"), balanceDisplay.readOnly().build(CURRENCY), //
				label.name("Total"), totalPaymentDisplay.readOnly().build(CURRENCY)));
	}

	@Override
	protected HBox trackedPane() {
		List<Node> l = new ArrayList<>(creationNodes());
		l.addAll(asList(//
				label.name("Last Modified by"), lastModifiedByDisplay.readOnly().width(120).build(TEXT), //
				label.name("on"), lastModifiedOnDisplay.readOnly().build(TIMESTAMP)));
		return box.forHorizontalPane(l);
	}

	@Override
	public void refresh() {
		super.refresh();
		idDisplay.setValue(service.getId());
		orderDateDisplay.setValue(service.getCreditDate());
		orderDatePicker.setValue(service.getCreditDate());
		valueField.setValue(service.getTotal());
		referenceField.setValue(service.getReference());
		descriptionField.setValue(service.getDescription());
		paymentTable.items(service.getPayments());
		lastModifiedByDisplay.setValue(service.getLastModifiedBy());
		lastModifiedOnDisplay.setValue(service.getLastModifiedOn());
	}

	@Override
	protected void setBindings() {
		setInputFieldBindings();
		orderDateBinding();
		valueField.disableIf(isPosted()//
				.or(totalPaymentDisplay.isNotEmpty())//
				.or(orderDatePicker.isEmpty()));
		referenceField.disableIf(isPosted()//
				.or(valueField.isEmpty()));
		descriptionField.disableIf(isPosted()//
				.or(referenceField.isEmpty()));
		paymentTable.disableIf(noDescription());
		saveButton.disableIf(noDescription());
	}

	private BooleanBinding noDescription() {
		return referenceField.isEmpty();
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		unpaidListButton.onAction(e -> unpaidListApp.addParent(this).start());
		unvalidatedListButton.onAction(e -> unvalidatedListApp.addParent(this).start());
		dataDumpButton.onAction(e -> dumpData());
		orderDatePicker.onAction(e -> setCreditDateUponUserValidation(orderDatePicker.getValue()));
		valueField.onAction(e -> service.updateTotals(valueField.getValue()));
		referenceField.onAction(e -> service.setReference(referenceField.getValue()));
		descriptionField.onAction(e -> service.setDescription(descriptionField.getValue()));
		paymentTable.setOnItemChange(i -> updatePaymentPane());
	}

	private void dumpData() {
		try {
			service.createAndWriteAnExcelDataDumpFile(dataDumpTable.build());
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}

	private void setCreditDateUponUserValidation(LocalDate d) {
		try {
			service.setCreditDateUponUserValidation(d);
		} catch (Exception e) {
			handleError(e, orderDatePicker);
		}
	}

	private void updatePaymentPane() {
		service.updatePayments(paymentTable.getItems());
		totalPaymentDisplay.setValue(service.getPayment());
		balanceDisplay.setValue(service.getBalance());
	}
}
