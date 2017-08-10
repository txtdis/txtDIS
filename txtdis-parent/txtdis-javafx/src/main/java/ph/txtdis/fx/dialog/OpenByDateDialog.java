package ph.txtdis.fx.dialog;

import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.LocalDatePicker;
import ph.txtdis.fx.pane.AppGridPane;

import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@Scope("prototype")
@Component("openByDateDialog")
public class OpenByDateDialog
	extends AbstractInputDialog {

	@Autowired
	private LocalDatePicker datePicker;

	private LocalDate date;

	@Override
	protected List<AppButton> buttons() {
		return singletonList(closeButton());
	}

	public LocalDate getDate() {
		return date;
	}

	@Override
	public void goToDefaultFocus() {
		datePicker.requestFocus();
	}

	@Override
	protected List<Node> nodes() {
		AppGridPane grid = pane.grid();
		grid.add(label.name(prompt), 0, 0);
		grid.add(datePicker(), 0, 1);
		return asList(header(), grid, buttonBox());
	}

	private LocalDatePicker datePicker() {
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
