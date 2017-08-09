package ph.txtdis.fx.dialog;

import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.LocalDatePicker;
import ph.txtdis.fx.pane.AppGridPane;

import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;

@Scope("prototype")
@Component("openByDateRangesDialog")
public class OpenByDateRangesDialog //
	extends AbstractInputDialog {

	@Autowired
	protected AppGridPane grid;

	@Autowired
	private LocalDatePicker startDatePicker, endDatePicker;

	private LocalDate startDate, endDate;

	public LocalDate getEndDate() {
		return endDate;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	@Override
	public void goToDefaultFocus() {
		startDatePicker.requestFocus();
	}

	@Override
	protected List<AppButton> buttons() {
		return asList(actionButton(), closeButton());
	}

	private AppButton actionButton() {
		AppButton actionButton = button.large("Generate").build();
		actionButton.onAction(event -> setDates());
		actionButton.disableIf(startDatePicker.isEmpty().or(endDatePicker.isEmpty()));
		return actionButton;
	}

	private void setDates() {
		startDate = startDatePicker.getValue();
		endDate = endDatePicker.getValue();
		refresh();
		close();
	}

	@Override
	public void refresh() {
		startDatePicker.setValue(null);
		endDatePicker.setValue(null);
		super.refresh();
	}

	@Override
	protected List<Node> nodes() {
		grid.getChildren().clear();
		grid.add(label.field("Start"), 0, 0);
		grid.add(startDatePicker, 1, 0);
		grid.add(label.field("End"), 0, 1);
		grid.add(endDatePicker(), 1, 1);
		return asList(header(), grid, buttonBox());
	}

	private Node endDatePicker() {
		endDatePicker.onAction(e -> validateDates());
		return endDatePicker;
	}

	private void validateDates() {
		if (endDatePicker.getValue() != null)
			try {
				ensureDateIsNotBeforeStart();
			} catch (Exception e) {
				resetNodesOnError(e);
			}
	}

	private void ensureDateIsNotBeforeStart() throws InvalidException {
		LocalDate start = startDatePicker.getValue();
		if (start == null)
			throw new InvalidException("Enter a start date first");
		if (endDatePicker.getValue().isBefore(start))
			throw new InvalidException("End date cannot be before start");
	}

	@Override
	protected void nullData() {
		super.nullData();
		startDate = null;
		endDate = null;
	}
}
