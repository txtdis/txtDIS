package ph.txtdis.fx.dialog;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import javafx.scene.control.Button;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.LocalDatePicker;
import ph.txtdis.fx.pane.AppGridPane;

@Scope("prototype")
@Component("openByDateDialog")
public class OpenByDateDialog
	extends AbstractInputDialog {

	@Autowired
	protected AppGridPane grid;

	@Autowired
	private LocalDatePicker datePicker;

	private LocalDate date;

	public LocalDate getDate() {
		return date;
	}

	@Override
	public void goToDefaultFocus() {
		datePicker.requestFocus();
	}

	@Override
	protected List<AppButton> buttons() {
		return singletonList(closeButton());
	}

	@Override
	protected List<Node> nodes() {
		grid.getChildren().clear();
		grid.add(label.name(prompt), 0, 0);
		grid.add(datePicker(), 0, 1);
		return asList(header(), grid, buttonBox());
	}

	private LocalDatePicker datePicker() {
		datePicker.setValue(null);
		datePicker.onAction(e -> onPick());
		return datePicker;
	}

	private void onPick() {
		date = datePicker.getValue();
		close();
	}

	@Override
	protected void nullData() {
		super.nullData();
		date = null;
	}

	@Override
	public void refresh() {
		super.refresh();
		datePicker.setValue(date);
	}
}
