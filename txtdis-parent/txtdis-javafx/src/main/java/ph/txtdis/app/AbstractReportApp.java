package ph.txtdis.app;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.dialog.OpenByDateRangesDialog;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.service.ReportService;

public abstract class AbstractReportApp<AT extends AppTable<T>, AS extends ReportService<T>, T>
		extends AbstractExcelApp<AT, AS, T> {

	@Autowired
	private AppButton backButton, openButton, nextButton;

	@Autowired
	private OpenByDateRangesDialog openDialog;

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

	protected void showPerSelectedDate() {
		try {
			showDataPerDate();
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}

	private void showPreviousDay() {
		try {
			service.previous();
			refresh();
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> superList = super.addButtons();
		List<AppButton> list = new ArrayList<>(asList(backButton, openButton, nextButton));
		list.addAll(superList);
		return list;
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
		return service.getHeaderText();
	}

	@Override
	protected String getTitleText() {
		return service.getTitleText();
	}

	@Override
	protected void setOnButtonClick() {
		backButton.setOnAction(e -> showPreviousDay());
		openButton.setOnAction(e -> showPerSelectedDate());
		nextButton.setOnAction(e -> showNextDay());
		super.setOnButtonClick();
	}

	@Override
	protected void setListeners() {
		table.setOnItemChange(e -> table.refresh());
	}
}
