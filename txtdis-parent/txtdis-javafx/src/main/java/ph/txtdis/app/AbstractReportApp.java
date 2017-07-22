package ph.txtdis.app;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.fx.dialog.OpenByDateRangesDialog;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.service.ReportService;

public abstract class AbstractReportApp<AT extends AppTable<T>, AS extends ReportService<T>, T> extends AbstractExcelApp<AT, AS, T> {

	@Autowired
	private AppButtonImpl backButton, openButton, nextButton;

	@Autowired
	private OpenByDateRangesDialog openDialog;

	@Override
	protected List<AppButtonImpl> addButtons() {
		List<AppButtonImpl> buttons = new ArrayList<>(asList(backButton, openButton, nextButton));
		buttons.addAll(super.addButtons());
		return buttons;
	}

	@Override
	protected void createButtons() {
		backButton.icon("back").tooltip("Back...").build();
		openButton.icon("openByDate").tooltip("Dates...").build();
		nextButton.icon("next").tooltip("Next...").build();
		super.createButtons();
	}

	@Override
	protected String getHeaderText() {
		return service.getHeaderName();
	}

	@Override
	protected String getTitleText() {
		return service.getTitleName();
	}

	@Override
	protected void setOnButtonClick() {
		backButton.onAction(e -> showPreviousDay());
		openButton.onAction(e -> showPerSelectedDate());
		nextButton.onAction(e -> showNextDay());
		super.setOnButtonClick();
	}

	private void showPreviousDay() {
		try {
			service.previous();
			refresh();
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}

	protected void showPerSelectedDate() {
		try {
			showDataPerDate();
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}

	private void showDataPerDate() throws Exception {
		displayOpenByDateDialog();
		setDates();
		refresh();
	}

	protected void setDates() {
		service.setStartDate(openDialog.getStartDate());
		service.setEndDate(openDialog.getEndDate());
	}

	protected void displayOpenByDateDialog() {
		openDialog.header("Enter Report Dates");
		openDialog.addParent(this).start();
	}

	private void showNextDay() {
		try {
			service.next();
			refresh();
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		table.setOnItemChange(e -> table.refresh());
	}
}
