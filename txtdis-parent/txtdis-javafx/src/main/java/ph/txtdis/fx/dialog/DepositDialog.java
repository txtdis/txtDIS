package ph.txtdis.fx.dialog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.fx.control.*;
import ph.txtdis.service.RemittanceService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import static ph.txtdis.type.Type.TIME;

@Scope("prototype")
@Component("depositDialog")
public class DepositDialog //
	extends AbstractFieldDialog<String> {

	@Autowired
	private LabeledCombo<String> bankCombo;

	@Autowired
	private LabeledDatePicker datePicker;

	@Autowired
	private LabeledField<LocalTime> timeInput;

	@Autowired
	private RemittanceService service;

	private ZonedDateTime timestamp;

	public String getBank() {
		List<String> customers = getAddedItems();
		return customers == null || customers.isEmpty() ? null : customers.get(0);
	}

	public ZonedDateTime getTimestamp() {
		return timestamp;
	}

	@Override
	protected AppButton addButton() {
		AppButton b = super.addButton();
		b.setText("Save");
		return b;
	}

	@Override
	protected void addItem() {
		try {
			LocalTime t = timeInput.getValue();
			LocalDate d = datePicker.getValue();
			timestamp = ZonedDateTime.of(d, t, ZoneId.systemDefault());
			super.addItem();
		} catch (DateTimeParseException e) {
			String s = "Incorrect time format;\nmust be 0808 for 8:08AM,\nand 2008 for 8:08PM.";
			messageDialog().showError(s).addParent(this).start();
			timeInput.setValue(null);
			timeInput.requestFocus();
		}
	}

	@Override
	protected List<InputNode<?>> addNodes() {
		bankCombo.name("Bank").items(service.listBanks()).build();
		datePicker.name("Date");
		timeInput.name("Time").build(TIME);
		return Arrays.asList(bankCombo, datePicker, timeInput);
	}

	@Override
	protected String createEntity() {
		return bankCombo.getValue();
	}

	@Override
	protected String headerText() {
		return "Enter Deposit Details";
	}
}
