package ph.txtdis.dyvek.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dyvek.service.DeliveryService;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.control.AppFieldImpl;

import java.math.BigDecimal;

import static ph.txtdis.type.Type.*;

@Scope("prototype")
@Component("deliveryApp")
public class DeliveryAppImpl
	extends AbstractOrderApp<DeliveryListApp, DeliveryService>
	implements DeliveryApp {

	@Autowired
	private AppCombo<String> recipientCombo;

	@Autowired
	private AppFieldImpl<BigDecimal> ffaInput, iodineInput, grossInput, tareDisplay;

	@Autowired
	private AppFieldImpl<String> plateInput, scaleInput, colorInput;

	@Override
	protected void firstGridLine() {
		comboAndInputGridNodes("Supplied by", customerCombo.width(310),
			"D/R No.", orderNoInput.width(110).build(TEXT), 0, 4);
		dateGridNodes("Date", orderDateDisplay, orderDatePicker, 7, 0, 2);
	}

	@Override
	protected void secondGridLine() {
		comboAndInputGridNodes("Delivered to", recipientCombo.width(310), "Item", itemCombo.width(110), 1, 4);
		qtyInKgInputGridNodes("Total", 7, 1);
	}

	@Override
	protected void thirdGridLine() {
		truckAndScaleGridNodes();
		qualityGridNodes();
		remarksGridNodes(4, 8);
	}

	private void truckAndScaleGridNodes() {
		labelGridNode("Truck", 0, 2);
		textInputGridNodes("Plate", plateInput, 1, 2);
		textInputGridNodes("Scale", scaleInput, 3, 2);
		qtyInKgInputGridNodes("Gross", grossInput, 5, 2);
		qtyInKgDisplayGridNodes("Tare", tareDisplay, 7, 2, 1);
	}

	private void qualityGridNodes() {
		labelGridNode("Quality", 0, 3);
		percentInputGridNodes("%FFA", ffaInput, 1, 3);
		qtyInputGridNodes(iodineInput);
		textInputGridNodes("Color", colorInput, 5, 3);
	}

	private void qtyInputGridNodes(AppField<BigDecimal> inputField) {
		labelGridNode("I₂V", 3, 3);
		gridPane.add(inputField.width(110).build(QUANTITY), 4, 3);
	}

	@Override
	@Lookup("deliveryListApp")
	protected DeliveryListApp orderListApp() {
		return null;
	}

	@Override
	public void refresh() {
		super.refresh();
		customerCombo.items(service.listCustomers());
		recipientCombo.items(service.listRecipients());
		plateInput.setValue(service.getTruckPlateNo());
		scaleInput.setValue(service.getTruckScaleNo());
		grossInput.setValue(service.getGrossWeight());
		refreshTare();
		ffaInput.setValue(service.getPercentFreeFattyAcid());
		iodineInput.setValue(service.getIodineValue());
		colorInput.setValue(service.getColor());
	}

	private void refreshTare() {
		tareDisplay.setValue(service.getTareWeight());
	}

	@Override
	protected void setBindings() {
		super.setBindings();
		recipientCombo.disableIf(orderDatePicker.disabledProperty());
		itemCombo.disableIf(customerCombo.isEmpty());
		qtyInput.disableIf(isPosted()
			.or(itemCombo.isEmpty()));
		plateInput.disableIf(isPosted()
			.or(qtyInput.isEmpty()));
		scaleInput.disableIf(isPosted()
			.or(plateInput.isEmpty()));
		grossInput.disableIf(isPosted()
			.or(scaleInput.isEmpty()));
		ffaInput.disableIf(isPosted()
			.or(grossInput.isEmpty()));
		iodineInput.disableIf(isPosted()
			.or(ffaInput.isEmpty()));
		colorInput.disableIf(isPosted()
			.or(iodineInput.isEmpty()));
		saveButton.disableIf(isPosted()
			.or(colorInput.isEmpty()));
		remarksDisplay.editableIf(isNew()
			.and(colorInput.isNotEmpty()));
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		recipientCombo.onAction(e -> service.setRecipient(recipientCombo.getValue()));
		plateInput.onAction(e -> service.setTruckPlateNo(plateInput.getValue()));
		scaleInput.onAction(e -> service.setTruckScaleNo(scaleInput.getValue()));
		grossInput.onAction(e -> updateGrossAndTare());
		colorInput.onAction(e -> service.setColor(colorInput.getValue()));
		iodineInput.onAction(e -> service.setIodineValue(iodineInput.getValue()));
		ffaInput.onAction(e -> service.setPercentFreeFattyAcid(ffaInput.getValue()));
	}

	private void updateGrossAndTare() {
		service.setGrossWeight(grossInput.getValue());
		refreshTare();
	}
}
