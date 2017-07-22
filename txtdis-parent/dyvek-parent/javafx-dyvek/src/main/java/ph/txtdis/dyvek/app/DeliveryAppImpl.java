package ph.txtdis.dyvek.app;

import static ph.txtdis.type.Type.FOURPLACE_PERCENT;
import static ph.txtdis.type.Type.QUANTITY;
import static ph.txtdis.type.Type.TEXT;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dyvek.service.DeliveryService;
import ph.txtdis.fx.control.AppCombo;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.control.AppFieldImpl;

@Scope("prototype")
@Component("deliveryApp")
public class DeliveryAppImpl //
		extends AbstractOrderApp<DeliveryListApp, DeliveryService> //
		implements DeliveryApp {

	@Autowired
	private AppCombo<String> recipientCombo;

	@Autowired
	private AppFieldImpl<BigDecimal> oleicInput, lauricInput, iodineInput, mvmInput, siInput;

	@Autowired
	private AppFieldImpl<String> plateInput, scaleInput, colorInput;

	@Override
	protected void firstGridLine() {
		comboAndInputGridNodes("Supplied by", customerCombo.width(310), "D/R No.", orderNoInput.width(110).build(TEXT), 0, 4);
		dateGridNodes("Date", orderDateDisplay, orderDatePicker, 7, 0, 2);
	}

	@Override
	protected void secondGridLine() {
		comboAndInputGridNodes("Delivered to", recipientCombo.width(310), "Item", itemCombo.width(110), 1, 4);
		qtyInKgInputGridNodes("Total", 7, 1);
	}

	@Override
	protected void thirdGridLine() {
		gridLine3();
		gridLine4();
		remarksGridNodes(4, 8);
	}

	private void gridLine3() {
		labelGridNode("Truck", 0, 2);
		textInputGridNodes("Plate", plateInput, 1, 2);
		textInputGridNodes("Scale", scaleInput, 3, 2);
		textInputGridNodes("Color", colorInput, 5, 2);
		qtyInputGridNodes("I₂V", iodineInput, 7, 2);
	}

	private void qtyInputGridNodes( //
			String qty, //
			AppField<BigDecimal> inputField, //
			int column, //
			int row) {
		labelGridNode(qty, column, row);
		gridPane.add(inputField.width(110).build(QUANTITY), ++column, row);
	}

	private void gridLine4() {
		labelGridNode("%FFA", 0, 3);
		percentInputGridNodes("Oleic", oleicInput, 1, 3);
		percentInputGridNodes("Lauric", lauricInput, 3, 3);
		fourPlacePercentInputGridNodes("%MVM", mvmInput, 5, 3);
		fourPlacePercentInputGridNodes("%SI", siInput, 7, 3);
	}

	private void fourPlacePercentInputGridNodes( //
			String percent, //
			AppField<BigDecimal> inputField, //
			int column, //
			int row) {
		labelGridNode(percent, column, row);
		gridPane.add(inputField.width(110).build(FOURPLACE_PERCENT), ++column, row);
	}

	@Override
	public void refresh() {
		super.refresh();
		customerCombo.items(service.listCustomers());
		recipientCombo.items(service.listRecipients());
		plateInput.setValue(service.getTruckPlateNo());
		scaleInput.setValue(service.getTruckScaleNo());
		colorInput.setValue(service.getColor());
		iodineInput.setValue(service.getIodineValue());
		oleicInput.setValue(service.getPercentOleicFreeFattyAcid());
		lauricInput.setValue(service.getPercentLauricFreeFattyAcid());
		mvmInput.setValue(service.getMoistureContent());
		siInput.setValue(service.getSaponificationIndex());
	}

	@Override
	protected void setBindings() {
		super.setBindings();
		recipientCombo.disableIf(orderDatePicker.disabledProperty());
		itemCombo.disableIf(customerCombo.isEmpty());
		qtyInput.disableIf(isPosted() //
				.or(itemCombo.isEmpty()));
		plateInput.disableIf(isPosted() //
				.or(qtyInput.isEmpty()));
		scaleInput.disableIf(isPosted() //
				.or(plateInput.isEmpty()));
		colorInput.disableIf(isPosted() //
				.or(scaleInput.isEmpty()));
		iodineInput.disableIf(isPosted() //
				.or(colorInput.isEmpty()));
		oleicInput.disableIf(isPosted() //
				.or(iodineInput.isEmpty()));
		lauricInput.disableIf(isPosted() //
				.or(oleicInput.isEmpty()));
		mvmInput.disableIf(isPosted() //
				.or(lauricInput.isEmpty()));
		siInput.disableIf(isPosted() //
				.or(mvmInput.isEmpty()));
		saveButton.disableIf(isPosted() //
				.or(oleicInput.isEmpty()));
		remarksDisplay.editableIf(isNew() //
				.and(siInput.isNotEmpty()));
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		recipientCombo.onAction(e -> service.setRecipient(recipientCombo.getValue()));
		plateInput.onAction(e -> service.setTruckPlateNo(plateInput.getValue()));
		scaleInput.onAction(e -> service.setTruckScaleNo(scaleInput.getValue()));
		colorInput.onAction(e -> service.setColor(colorInput.getValue()));
		iodineInput.onAction(e -> service.setIodineValue(iodineInput.getValue()));
		oleicInput.onAction(e -> service.setPercentOleicFFA(oleicInput.getValue()));
		lauricInput.onAction(e -> service.setPercentLauricFFA(lauricInput.getValue()));
		mvmInput.onAction(e -> service.setMoistureContent(mvmInput.getValue()));
		siInput.onAction(e -> service.setSaponificationIndex(siInput.getValue()));
	}
}
